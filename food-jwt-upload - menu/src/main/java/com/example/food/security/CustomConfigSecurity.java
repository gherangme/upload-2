package com.example.food.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //prePostEnabled = true: cho phép chạy lại
public class CustomConfigSecurity {

    @Autowired
    CustomAuthentication authProvider;

    @Autowired
    CustomFilterJwt customFilterJwt;

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider); //Ghi đè lên AuthenticationManager để khi nó đọc
        //nó sẽ đọc vào phần Custom của mình chứ không phải Custom mặc định
        return authenticationManagerBuilder.build();
    }

    /*
        - Config bằng code java
        - Khi spring boot start lên sẽ đọc các Annotation config trước rồi đến các
            annotation đưa lên BEAN

        - ConfigureGlobal: sẽ tạo ra 1 list user được phép đăng nhập và list này được lưu ở RAM
        -> Chúng ta sẽ custom AuthenticationManagerBuilder

        -SecurityFilterChain: định nghĩa link nào chứng thực hay không chứng thực mới được vào
            http.authorizeRequests(): đang sử dụng chứng thực
            .antMatchers.permitAll("/asda"): định nghĩa link nào được truy cập không cần chứng thực
            .anyRequest(): tất cả request còn lại đều phải chứng thực
            .httpBasic(): chuẩn chứng thực là BasicAuthen
            .authenticationEntryPoint(authenticationEntryPoint): dùng để can thiệp lỗi 404 (hiểu thì tốt ko thì skip)
            .http.addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class): không add cũng được

            Lưu ý:
                + .csrf() là một dạng chống tấn công theo kiểu ví dụ đăng nhập vào rồi ở máy náy có cái token và copy cái token
                qua máy khác thì máy khác không gọi được api này
                + Vì Postman được xem là một cái máy khác nên phải disable đi để có thể vào được
                + Chỉ sử dụng Web và api chung 1 page thì xài được vì khi vô page của mình thì session sẽ giữ trên page và
                khi đăng nhập thì token nằm trên page -> tương tác bth nhưng ở đây đang viết api thì chúng ta disable đi để token đó
                qua máy khác cũng có thể truy cập được

        - Tóm lại: cần 2 thằng chính là configureGlobal và SecurityFilterChain để cấu hình
            (ngoài ra cần thêm một số nhỏ và đưa lên BEAN như PasswordEncoder)

        - LỖI:
            + 401: chưa chứng thực (tài khoản không có trong hệ thống)
            + 403: đã chứng thực nhưng không có quyền (dính đến role) (tài khoản đã có trong hệ thống nhưng không có quyền)
     */

    /*
        AUTHORICATION
		- ngoài việc chứng thực xong thì có một vài api hay 1 vài hàm phải có quyền A, B ... mới cho thực hiện API đó
		- VD Link đăng ký phải có quyền ADMIN thì mới đc phép thực hiện link đăng ký còn không sẽ báo không có quyền
	    - Có 2 cách định nghĩa:
	        + 1. Định nghĩa ở filterChain
	        + 2. Định nghĩa bằng Annotation
	        + Trỗn lẫn cả 2 vì role và author là 2 cái khác nhau
	 */

    //Cách 1
    //Vì @Autowired configGlobal không được nên copy vào chỉnh lại
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        /*
//            Tạo danh sách tài khoản Global được phép truy cập
//         */
//        auth
//                .inMemoryAuthentication()
//                .withUser("user1")
//                .password(new BCryptPasswordEncoder().encode("user1Pass"))
//                .authorities("ROLE_USER");
//    }

    //Cách 2
    //PasswordEncoder encoder đã được đưa lên BEAN, muốn dùng thì gọi vào trong hàm nó sẽ tự hiểu
//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
//        UserDetails admin = User.withUsername("hach")
//                .password(encoder.encode("hacheery"))
//                .authorities("ADMIN") //ở filterChain line 134 dùng .hasAuthority("ADMIN") nên đây phải là .authorities("ADMIN")
////                .roles("ADMIN")
//                .build();
//        UserDetails user = User.withUsername("user")
//                .password(encoder.encode("pwd1"))
//                .roles("USER") //Security 5: .roles("ROLE_USER")
//                .build();
//        return new InMemoryUserDetailsManager(admin, user);
//        /*
//            Khi đăng nhập Auth Basic thành công thì nó lưu vào Session nên khi đăng nhập lại nếu username
//            không đổi mà đổi password thì nó vẫn chấp nhận vì nó kiểm tra Session
//         */
//    }
//Vì chứng thực trực tiếp rồi nên ko cần UserDetailsService hoặc configGlobal
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*
            Chứng thực link nào được truy cập, link nào không
         */

        /*
            Nếu không có .permitAll().anyRequest().authenticated().and().httpBasic();
            thì mọi link con trong login đều truy cập được nếu đế .antMatchers("/login")

            Nếu có .permitAll().anyRequest().authenticated().and().httpBasic();
            thì mọi link con trong login đều bắt buộc phải chứng thực
         */

        /*
            - 2 kiểu:
            + hasAuthor, hasAnyAuthor: có quyển truy cập 1 link cụ thể nào đó hay không
            + hasRole: 1 role, hasAnyRole (String... tên tham số): đại diện cho tham số là 1 mảng, nhận vào mảng String
            Về cơ bản không khác nhau

            (ĐỐI VỚI SRING SECURITY 5
            - Cách sử dụng: để dùng hasRole hoặc hasAnyRole thì tên (line 86) luôn luôn phải có prefix ROLE_tên
                VD: ROLE_ADMIN, ROLE_USER, ...
            còn hasAuthor thì không cần) LƯU Ý: Ở SECURITY 6 THÌ KO CẦN PREFIX NÓ CŨNG HIỂU
            -> Role có quyền thực hiện 1 chức năng nào đó như thêm xóa sửa tùy theo số lượng role qui định thường dùng
            cho role động
            -> Dùng Author nhiều hơn

            - Tên role thường mô tả cho việc có quyền để làm 1 chức năng nào đó như thêm, xóa, sửa, ... thì dùng role
            trong các trường hợp này
         */
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)// Hủy sử dụng session
                .and()
                .authorizeRequests()
                    .antMatchers("/login/signin")// /login/**: login/1, login/2 được phép truy cập
                    .permitAll()
//                    .antMatchers("/login/signup")
//                    .hasAnyRole("ROLE_ADMIN", "ROLE_USER") // có hasRole thì không cần authenticated nữa
//                    //Muốn quy định thêm thì antMatchers().hasAuthor()......
//                    .hasAuthority("ADMIN")
                    //Cách 1: 4 dòng trên là cách quy định ở ConfigSecurity
                    //Cách 2: quy định trong controller
                    .antMatchers("/menu/files/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated(); //Áp dụng filter thì không cần httpBasic
//                .and().httpBasic();

        http.addFilterBefore(customFilterJwt, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //BCrypt mã hóa 1 chiều, chỉ mã hóa đc nhưng không giải mã được
    }

}

package com.example.food.security;

import com.example.food.utils.JwtUtilsHelpers;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
/*
    - Cách 1 dùng @Bean đưa lên Container
    - Cách 2 dùng @Component
 */
public class CustomFilterJwt extends OncePerRequestFilter {

    private Gson gson = new Gson();

    @Autowired
    JwtUtilsHelpers jwtUtilsHelpers;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            boolean isSuccess = jwtUtilsHelpers.verifyToken(jwt);

            //Kiểm tra data lấy ra từ token
            //Jwt còn hiệu lực thì còn tạo và set chứng thực cho nó này không cần username và password
            if(isSuccess) {
                String data = jwtUtilsHelpers.getDataFromToken(jwt);

                //Biến chuỗi Json về lại thành class Authentication vì trước đó mình đã parse để lấy data trong token
//                Authentication authentication = gson.fromJson(data, Authentication.class);

                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("", "", new ArrayList<>());// new ArrayList<>() sẽ lưu ds role của người dùng
                //mình duyệt và xử lý nó là xong
                SecurityContext securityContext = SecurityContextHolder.getContext();
                securityContext.setAuthentication(token);

                System.out.println("Hello filter: "+data);

                /*
                    Đến đây là xong cơ chế JWT rồi
                    - Tạo token
                    - Thông qua Filter lấy token và giải mã
                    - Giải mã thành công thì chứng tỏ đăng nhập thành công
                    - Lúc này tạo chứng thực để cho spring biết rằng user này đã đăng nhập thành công rồi và đc phép vào link này
                 */
            }

            //Tới đây kiểm tra xem chuỗi token truyền đúng không, rồi thêm kí tự cho token sai nó sẽ như thế nào
        }catch (Exception e) {

        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }
}

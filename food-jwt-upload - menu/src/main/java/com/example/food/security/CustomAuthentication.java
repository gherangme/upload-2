package com.example.food.security;

import com.example.food.service.imp.LoginServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

//Không xử lý logic code (@Service), không sử dụng database(@Repository), @Bean được tạo trong 1 class, tức là hàm trả ra 1 đối tượng
//muốn đưa đối tượng đó lên BEAN thì dùng BEAN
//-> Dùng @Component: dùng cho cả 1 class
@Component
public class CustomAuthentication implements AuthenticationProvider {
    /*
        - supports: hỗ trợ dạng so sánh chứng thực
     */

    @Autowired
    LoginServiceImp loginServiceImp;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        /*
            - Xử lý đăng nhập thành công, thất bại ở đây
            - Nhiệm vụ của thằng Authen này query database chứng thực ra trả ra chứng thực UsernamePasswordAuthenticationToken
            là xong nhiệm vụ
            Third-Party System: là Database, hoặc trong microservice thì chứng thực trên Server của MService
         */

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (loginServiceImp.login(username, password)) {
            System.out.println(username + " - " + password);
            return new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
        }else {
            return null; //Khi return null security tự hiểu user này không tồn tại
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

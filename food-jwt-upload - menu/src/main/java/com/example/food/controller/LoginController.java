package com.example.food.controller;

import com.example.food.payload.ResponseData;
import com.example.food.utils.JwtUtilsHelpers;
import com.google.gson.Gson;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtilsHelpers jwtUtilsHelpers;

    //permitAll()
    //authen()
    @PostMapping("/signin")
    public ResponseEntity<?> signin(
            @RequestParam String username,
            @RequestParam String password
    ) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        /*
            Nếu authenticate trả ra 1 chứng thực thành công thì sẽ lưu lên SecurityContext,
                để lưu vào SecurityContext thì mình phải tạo ra 1 SecurityContext
                lấy context mặc định của spring boot sau đó set chứng thực bằng cái chứng thực nó trả ra cho mình
         */

        //tạo chứng thực
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        //Tạo key sử dụng cho JWT
//        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//        String jwtKey = Encoders.BASE64.encode(key.getEncoded());
//        System.out.println("Key: "+jwtKey);
        /*
            - Tạo ra được key rồi gọi tới link signin để phát ra key và lưu key vào yml
            -> Vì key không được thay đổi liên tục, một server chỉ được phép có 1 key thôi và token phải được generate trên key này
            - Sau khi lưu trữ rồi thì tắt nó đi, không cần nữa
         */
        /*
            TOKEN
            - Tạo ra token ra và giải mã token -> Đang có 2 thao tác xử lý token -> tách ra 1 class riêng để xử lý trong tái sử dụng
         */
        Gson gson = new Gson(); // Vì khi kèm username và password vào token để lưu trữ có khả năng bị mã hóa nên parse toàn bộ về json và truyền vào
        String data = gson.toJson(authentication);

        System.out.println("Data: "+data);

        ResponseData responseData = new ResponseData();
        responseData.setData(jwtUtilsHelpers.generateToken(data));

        return new ResponseEntity<>(responseData, HttpStatus.OK);
        /*
            - đến đây (responseData): khi front-end gọi đến login và đã trả ra được token -> FE lưu token lại ở Headers
            tại Authorization

            - Trong Servlet thì mình có thể nhận đc headers, nhận tham số trong filter để xử lý
            - Trong cơ chế JWT: trong filter mình sẽ lấy headers ra và lụm token ra và giải mã token đó, nếu như thành công
            thì tạo chứng thực, thất bại thì thôi
         */
    }

    @PostMapping("/signup")
//    @PreAuthorize("hasAnyAuthority('ADMIN')") //Trong spring gọi là express security, có thể khai báo biến, gọi hàm ở trong đây
    public ResponseEntity<?> signup() {

        return new ResponseEntity<>("Hello signup", HttpStatus.OK);
    }

}

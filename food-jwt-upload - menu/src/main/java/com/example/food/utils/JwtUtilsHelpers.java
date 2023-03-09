package com.example.food.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Calendar;
import java.util.Date;

/*
    Các class tái sử dụng lại sẽ được đặt trong package utils
 */
@Component
public class JwtUtilsHelpers {

    /*
        @Value: Khi định nghĩa những config trong yml, muốn lấy giá trị của nó ra để xài thì dùng
        annotation value thì dùng @Value("${tên}")
     */
    //Tương tác tạo ra token và giải mã token ở đây
    @Value("${jwt.privateKey}")
    private String privateKey;

    private long expiredTime = 8 * 60 * 60 * 1000;
//    private long expiredTime = 20 * 1000;

    public String generateToken(String data) {
        System.out.println("Kiem tra: "+privateKey);

        SecretKey key = Keys.hmacShaKeyFor(privateKey.getBytes());
        //= SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(privateKey));

//        Calendar calendar = Calendar.getInstance();
//        calendar.getTimeInMillis();
        Date date = new Date();
        long currentDateMilis = date.getTime() + expiredTime;
        Date expiredDate = new Date(currentDateMilis);

        String jwt = Jwts.builder()
                .setSubject(data) //Dữ liệu muốn lưu kèm sau này lấy ra sử dụng
                .signWith(key) //Key mã hóa
                .setExpiration(expiredDate) //Set time
                .compact();

        System.out.println("Token: "+jwt);
        // Lúc này có token rồi, ko trả ra hello login nữa mà trả ra token cho front-end lưu trữ
        return jwt;
    }

    public boolean verifyToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(privateKey.getBytes());
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    public String getDataFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(privateKey.getBytes());
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
        }catch (Exception e) {
            return "";
        }
    }

}

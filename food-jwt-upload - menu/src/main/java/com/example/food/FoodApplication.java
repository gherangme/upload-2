package com.example.food;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // Mem cache
public class FoodApplication {
	/*
		- Cách tích hợp security
			copy dependency trong pom vào pom và click m-xanh
			khi start lên ra Using generated security password: 46010301-1ac6-4b27-863e-94e0b3fa240d là đúng

		- Theo cấu trúc thư mục thì package controller sẽ chứa các api
	 */

	/*
		- Tạo riêng 1 package security để cấu hình
	 */

    /*
        HÌNH SECURITY
        Cơ chế hoạt động của spring security:
        + Khi một request của client gọi tới thì nó đi vào hệ thống filter (để chống tấn công csrf, logout filter, ...)
        + Hệ thống filter này sẽ chạy tiếp vào AuthenticationManage để tạo ra các chứng thực
        + Để lấy ra database nó dùng UserDetailService -> Vì UserDetailService tốn thêm thao tác mà không Custom được nhiều
        -> Nên mình sẽ Custom ngay tại AuthenticationManager, sau khi Custom và mình báo chứng thực thành công thì mình trả
        ngược cho filter và filter nó sẽ lưu chứng thực đó ở trên context.

        Chắc chắn UserDetailService nó sẽ thông qua Manager để chứng thực -> Thay vì vậy lược bỏ UserDetailService và chứng
        thực tại AuthenticationManager -> Kết hợp truyền username, password xuất ra
     */

	/*
		- Mặc định security khi đăng nhập vô nó sẽ tạo ra Session và lưu trên RAM, khi chứng thực đăng nhập xong nó sẽ lấy token
		đăng nhập tạo ra và lên RAM so sánh, lúc này trên SecurityContextHolder chưa có gì vì mình đã bỏ UserDetailService -> Không có, lỗi 401
		- Phải hủy Session để khi mình đưa lên nó giữ ở SecurityContextHolder
		- LỢI ÍCH HỦY SESSION:
			+ Tránh được những catch vô lý của security
			+ Session sẽ tốn nhiều RAM
			+ Nếu làm về Microservice -> kỵ nhau
	 */

	/*
		JWT
		- Khi hủy Session làm sao để biết User đó đăng nhập thành công ? Khi gọi link đó để biết rằng user đã đc chứng thực
			trước đó và cách tận dụng đươc security
		- Session được lưu trên RAM -> Vấn đề lớn khi catch thì không control đc

		- Nhiệm vụ là khi người dùng đăng nhập lần đầu tiên, làm cách nào để người dùng gọi vào link khác để mình kiểm chứng xem
			user đã đăng nhập rồi hay chưa để mình cho phép người dùng truy cập vào link đó.
			-> Lúc này sẽ có khái niệm Token: là khi một ng dùng gọi link login thì lúc này chứng thực xem ngta login thành công hay chưa
			thành công -> Trả cho ngta 1 token đc mã hóa thông tin trong đó như user, infor... và trả ra cho front-end xử lý, FE lưu token lại
			khi user gọi đến link khác thì không cần chứng thực nữa mà truyền token kèm này lên, Filter sẽ kiểm tra xem token này có phải do hệ thống
			mình sinh ra hay không, nếu phải thì đăng nhập thành công ko thì thất bại.
			-> Đăng nhập thành công thì cung cấp token, mình giải mã đc thì đăng nhập thành công, ko đc thì thất bại, khi front-end gọi đến link
			login của mình và đăng nhập thành công thì trả ra cho họ token, FE sẽ lưu lại token và khi gọi các url mà có chứng thực thì họ sẽ truyền token
			này lên. Lúc này Filter sẽ xử lý giải mã token này đc không? Nếu đc thì do hệ thống mình sinh ra, có một khóa chính để giải mã và đc lưu ở server
			dùng để giải mã token.
		Lợi ích:
			+ Không cần đăng nhập lại nữa mà chỉ cần truyền token này lên, vì khi lưu trên ram thì tốn ram còn token chỉ xử lý giải mã là xong
			+ Cấp thời gian hết hạn cho token VD chỉ cho phép dùng token trong 8h, nếu hết thời gian sẽ bắt đăng nhập lại

		*Làm sao sinh ra đc token ?
			+ Tự chế thuật toán
			+ Dùng thư viện jjwt: giúp mình sinh ra token, thông qua thư viện này mình làm đc expire date, và lưu trữ đc dữ liệu trong token này

	 */
	public static void main(String[] args) {
		SpringApplication.run(FoodApplication.class, args);
	}

}

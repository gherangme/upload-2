package com.example.food.controller;

import com.example.food.payload.ResponseData;
import com.example.food.service.imp.FileStorageServiceImp;
import com.example.food.service.imp.MenuServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/menu")
public class MenuController {

    /*
        2 Cách upload file:
            + Post form-data: - truyền theo kiểu stream giúp mình tiết kiệm dung lượng bộ nhớ, ko bị hết ram khi xử lý
            (cách này đc dùng nhiều và phổ biến nhất) và đối với upload nhiều file thì dùng cách này

            + Request body: - nhược điểm: phải chuyển file về base-64 -> sẽ bị x1.5 dung lượng file
                                        -> dung lượng request lớn -> băng thông nghẽn xử lý lâu
                            - ưu điểm: muốn truyền bao nhiêu file cũng được, truyền kèm dữ liệu cũng đc
                                        do truyền theo kiểu có cấu trúc nên có thể biến thành đối tượng xử lý đc
                             {
                                "file": "asdasd",
                                "name": "asdasd"
                             }

     */

    /*
        - MultipartFile file: lấy file từ postman
        - Lưu file ở đâu?: 1. Lưu ở database: file phải chuyền về base-64 hoặc file phải ở dạng byte (cách này không khuyến khích
                                vì dữ liệu càng nặng, query càng lâu)
                                * Đặc điểm của database:+ Luôn chỉ định cột cần lấy ra để giới hạn dữ liệu trả ra cho câu query nhanh hơn
                                                        + Dữ liệu càng nặng lấy ra càng lâu
                           2. Lưu ở ổ đĩa: file upload sẽ được lưu vào ổ đĩa cứng (cách này luôn sử dụng)
     */

    @Autowired
    MenuServiceImp menuServiceImp;

    @Autowired
    FileStorageServiceImp fileStorageServiceImp;

    @PostMapping("") //file có khi 1 2GB get ko đáp ứng được -> POST
    public ResponseEntity<?> addMenu(@RequestParam MultipartFile file,
                                     @RequestParam String name,
                                     @RequestParam String description,
                                     @RequestParam double price,
                                     @RequestParam String instruction,
                                     @RequestParam int cate_res_id) {
        System.out.println("kiem tra " + file.getOriginalFilename());
        boolean isSuccess = menuServiceImp.insertFood(file,name,description,price,instruction,cate_res_id);
        ResponseData responseData = new ResponseData();
        responseData.setData(isSuccess);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("")
    private ResponseEntity<?> getAllMenu() {
        ResponseData responseData = new ResponseData();
        responseData.setData(menuServiceImp.getAllFood());

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/files/{fileName:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        Resource resource = fileStorageServiceImp.load(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
        /*
            Hạn chế request size để tránh user spam làm hệ thống chậm
            thêm vào yml:
            max-file-size (dung lượng tối đa)
         */
        /*
            BÀI TẬP:
            + Ở API ADD MENU CHO PHÉP NGƯỜI DÙNG TẠO RA MỘT MENU MỚI VÀ UPLOAD HÌNH ẢNH CỦA MENU NÀY
            + LẤY DANH SÁCH MENU VÀ HÌNH ẢNH CỦA MENU
         */
        /*
            BUỔI SAU:
            + GHI LOG LỖI
            + GIẢI API
            + CƠ CHẾ CATCH: 1 CATCH TRÊN RAM, 2 CATCH BẰNG MỘT HỆ THỐNG KHÁC (HỆ THỐNG LÀ NƠI MÌNH LƯU TRỮ THÔNG TIN CATCH RIÊNG BIỆT
            VÀ CÓ HỆ ĐIỀU HÀNH RIÊNG BIỆT. (REDIS)
         */
    }

}

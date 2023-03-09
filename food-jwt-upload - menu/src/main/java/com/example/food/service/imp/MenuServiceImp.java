package com.example.food.service.imp;

import com.example.food.dto.FoodDTO;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MenuServiceImp {

    boolean insertFood(MultipartFile file, String name, String desc, double price, String instruction, int cate_res_id);
    List<FoodDTO> getAllFood();

}

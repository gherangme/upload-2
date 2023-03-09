package com.example.food.service;

import com.example.food.dto.FoodDTO;
import com.example.food.entity.CategoryRestaurant;
import com.example.food.entity.Food;
import com.example.food.repository.FoodRepository;
import com.example.food.service.imp.FileStorageServiceImp;
import com.example.food.service.imp.MenuServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class MenuService implements MenuServiceImp {

    @Autowired
    FileStorageServiceImp fileStorageServiceImp;

    @Autowired
    FoodRepository foodRepository;

    @Override
    public boolean insertFood(MultipartFile file, String name, String desc, double price, String instruction, int cate_res_id) {

        boolean isInsertSuccess = false;
        boolean isSuccess = fileStorageServiceImp.saveFiles(file);


        if(isSuccess) {
            try {
                //Luu du lieu khi file da duoc save thanh cong
                Food food = new Food();
                food.setName(name);
                food.setDesc(desc);
                food.setPrice(price);
                food.setIntruction(instruction);
                food.setImage(file.getOriginalFilename());

                CategoryRestaurant categoryRestaurant = new CategoryRestaurant();
                categoryRestaurant.setId(cate_res_id);

                food.setCategoryRestaurant(categoryRestaurant);

                foodRepository.save(food);

                isInsertSuccess = true;
            }catch (Exception e) {
                System.out.println("Error insert food "+e.getMessage());
            }
        }

        return isInsertSuccess;
    }

    @Override
    @Cacheable("food")
    public List<FoodDTO> getAllFood() {
        System.out.println("Kiem tra cache");
        List<Food> list = foodRepository.findAll();
        List<FoodDTO> dtoList = new ArrayList<>();
        for (Food food: list) {
            FoodDTO foodDTO = new FoodDTO();
            foodDTO.setImage(food.getImage());
            foodDTO.setName(food.getName());

            dtoList.add(foodDTO);
        }

        return dtoList;
    }
}

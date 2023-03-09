package com.example.food.service;

import com.example.food.service.imp.FileStorageServiceImp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService implements FileStorageServiceImp {

    //Path giúp định nghĩa đường dẫn, file đc lưu ở đâu
    @Value("${upload.path}")
    private String path;
    private Path root; //Lúc tạo sẽ tạo ra trong file của mình rồi mình trỏ ra desktop

    @Override
    public boolean saveFiles(MultipartFile file) {
        try {
            System.out.println("Kiemtra "+path);
            init();
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING); //resolve: /uploads/file_name_user
            return true;
        }catch (Exception e) {
            System.out.println("Error save file: "+e.getMessage());
            return false;
        }
    }

    @Override
    public Resource load(String fileName) {
        try {
            /*
                Trong spring boot hỗ cho mình nhiều định dạng và trả ra cho ng dùng cái file
             */
            Path file = root.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) { //isReadable nếu tồn tại hoặc có thể đọc được thì mới lấy
                return resource;
            }else {
                return null;
            }
        }catch (Exception e) {
            System.out.println("Error load file "+e.getMessage());
            return null;
        }
    }

    private void init() {
        try {
            root = Paths.get(path);
            if(!Files.exists(root)) {
                Files.createDirectories(root);
            }
        }catch (Exception e) {
            System.out.println("Error create root folder "+e.getMessage());
        }
    }

}

package com.example.food.repository;

import com.example.food.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//tạo interface trong repository để khử dependency
@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {

    List<Users> findByEmailAndPassword(String username, String password);

}

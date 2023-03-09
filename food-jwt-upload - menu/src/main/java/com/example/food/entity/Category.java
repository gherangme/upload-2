package com.example.food.entity;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String desc;

    @OneToMany(mappedBy = "category")
    private Set<CategoryRestaurant> listCategoryRestaurants;

    public Set<CategoryRestaurant> getListCategoryRestaurants() {
        return listCategoryRestaurants;
    }

    public void setListCategoryRestaurants(Set<CategoryRestaurant> listCategoryRestaurants) {
        this.listCategoryRestaurants = listCategoryRestaurants;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

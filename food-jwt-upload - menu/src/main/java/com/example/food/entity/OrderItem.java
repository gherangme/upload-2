package com.example.food.entity;

import com.example.food.entity.keys.KeyOrderItem;

import javax.persistence.*;

@Entity(name = "orders_item")
public class OrderItem {

    @EmbeddedId
    private KeyOrderItem keys;

    @Column(name = "amount")
    private int amount;

    @Column(name = "price")
    private double price;

    @ManyToOne
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Orders orders;

    @ManyToOne
    @JoinColumn(name = "food_id" , insertable = false, updatable = false)
    private Food food;

    public KeyOrderItem getKeys() {
        return keys;
    }

    public void setKeys(KeyOrderItem keys) {
        this.keys = keys;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }
}

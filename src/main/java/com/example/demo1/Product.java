package com.example.demo1;

public class Product {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private double finalPrice;
    private String date;

    public Product(int id, String name, double price, int quantity, double finalPrice) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.finalPrice = finalPrice;
//        this.date = date;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public double getPrice() {
        return price;
    }
    public int getQuantity() {
        return quantity;
    }
    public double getFinalPrice() {
        return finalPrice;
    }
    public String getDate() {
        return date;
    }
}

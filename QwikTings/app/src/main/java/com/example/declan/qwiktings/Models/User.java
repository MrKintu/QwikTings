package com.example.declan.qwiktings.Models;

public class User
{
    private Long phone;

    public User() {
    }

    public User(Long phone) {
        this.phone = phone;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }
}
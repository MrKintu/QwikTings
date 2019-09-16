/*
 * Copyright (c) 2019. This code and all it's likeness are property of Kintu Declan Trevor.
 * d-kintu@outlook.com
 * Any unauthorised use of this material shall lead to legal prosecution. All rights reserved.
 */

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
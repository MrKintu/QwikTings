/*
 * Copyright (c) 2019. This code and all it's likeness are property of Kintu Declan Trevor.
 * d-kintu@outlook.com
 * Any unauthorised use of this material shall lead to legal prosecution. All rights reserved.
 */

package com.example.declan.qwiktings.Models;

public class Category
{
    private String name;
    private String image;

    public Category() {}

    public Category(String name, String image)
    {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

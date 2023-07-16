package com.example.demo.dto.request;

import com.example.demo.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackageForm {

    private Long id;
    private String name;
    private int duration;
    private Double price;
    private int dowloads;
    private Double storageSize;
    private boolean active;
    private boolean type;

    public PackageForm() {
    }

    public PackageForm(String name, int duration, Double price, int dowloads, Double storageSize) {
        this.name = name;
        this.duration = duration;
        this.price = price;
        this.dowloads = dowloads;
        this.storageSize = storageSize;
    }

}

package com.example.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayForm {
    private Long user_id;
    private Long package_id;
    private Long file_id;
    private String name;

    public PayForm() {
    }

    public PayForm(Long user_id, Long package_id, Long file_id, String name) {
        this.user_id = user_id;
        this.package_id = package_id;
        this.file_id = file_id;
        this.name = name;
    }

}
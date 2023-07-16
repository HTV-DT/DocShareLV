package com.example.demo.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsResponse {
    private Long total_order;
    private float total_price;
    private Long total_user;
    private Double total_view;
     private Long total_dowload;
       private Long total_like;


    public StatisticsResponse() {
    }

    public StatisticsResponse(Long total_order, float total_price, Long total_user, Double total_view) {
        this.total_order = total_order;
        this.total_price = total_price;
        this.total_user = total_user;
        this.total_view = total_view;
    }

}

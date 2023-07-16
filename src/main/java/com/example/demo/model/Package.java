package com.example.demo.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotBlank;

import com.example.demo.utils.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.OrderInfoView.class)
    private Long id;

    @JsonView(Views.OrderInfoView.class)
    private String name;

    @JsonView(Views.OrderInfoView.class)
    private int duration;

    @JsonView(Views.OrderInfoView.class)
    private Double price;

    @JsonView(Views.OrderInfoView.class)
    private int dowloads;

    @JsonView(Views.OrderInfoView.class)
    private Double storageSize;

    private boolean active;

    private int type;//0 Ko giới hạn , 1 giới hạn ,2 upload

    //   @Transient
    // private Long total_user;



    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "packages", cascade = CascadeType.ALL)
    Set<Order> orders;

    public Package() {
    }

    public Package(String name, int duration, Double price, int dowloads, Double storageSize) {

        this.name = name;
        this.duration = duration;
        this.price = price;
        this.dowloads = dowloads;
        this.storageSize = storageSize;
    }

public int getOrderCount() {
    int count = 0;
    if (orders != null) {
        for (Order order : orders) {
            if (order.isOrderStatus()) {
                count++;
            }
        }
    }
    return count;
}

    // public Long getTotal_user(EntityManager em) {
    //     CriteriaBuilder builder = em.getCriteriaBuilder();
    //     CriteriaQuery<Long> query = builder.createQuery(Long.class);
    //     Root<Order> root = query.from(Order.class);
    //     query.select(builder.countDistinct(root.get("user")))
    //             .where(builder.equal(root.get("packages"), this));
    //     return em.createQuery(query).getSingleResult();
    // }

    // public void setTotal_user(EntityManager em) {
    //     CriteriaBuilder builder = em.getCriteriaBuilder();
    //     CriteriaQuery<Long> query = builder.createQuery(Long.class);
    //     Root<Order> root = query.from(Order.class);
    //     query.select(builder.countDistinct(root.get("user")))
    //          .where(builder.equal(root.get("packages"), this));
    //     Long count = em.createQuery(query).getSingleResult();
    //     this.total_user = count;
    // }





}

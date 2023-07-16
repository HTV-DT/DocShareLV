
package com.example.demo.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "access")
@Getter
@Setter
public class Access {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private Package packages;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "num_of_access")
    private int numOfAccess;
    @Transient
    private String name;
    @Transient
    private Double price;
    @Transient
    private int dowloads;
    @Transient
    private Double storageSize;

    public Access() {
    }

    public Access(Long id, Users user, LocalDateTime createdAt, int numOfAccess) {
        this.id = id;
        this.user = user;
        this.createdAt = createdAt;
        this.numOfAccess = numOfAccess;
    }

    public String getName() {
        return this.packages.getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return this.packages.getPrice();
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getId() {
        return this.id;
    }

    public int getDowloads() {
        return this.packages.getDowloads();
    }

    public void setDowloads(int dowloads) {
        this.dowloads = dowloads;
    }

    public Double getStorageSize() {
        return this.packages.getStorageSize();
    }

}
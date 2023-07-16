package com.example.demo.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

import com.example.demo.utils.Views;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({ Views.FileInfoView.class, Views.FileInfoViewAdmin.class })
    private Long tagId;
    @NotBlank
    @JsonView({ Views.FileInfoView.class, Views.FileInfoViewAdmin.class })
    private String tagName;
    @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    private Set<File> files = new HashSet<>();

    public Tag() {
    }

    public Tag(String tagName) {

        this.tagName = tagName;
    }

}
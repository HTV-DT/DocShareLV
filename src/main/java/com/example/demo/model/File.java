package com.example.demo.model;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.example.demo.utils.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class File {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonView({ Views.FileInfoView.class, Views.FileInfoViewAdmin.class })
  private Long id;
  @NotBlank
  @JsonView({ Views.FileInfoView.class, Views.FileInfoViewAdmin.class })
  @Column(length = 100)
  private String fileName;
  private String fileType;
  private Double fileSize;
  @JsonView({ Views.FileInfoView.class, Views.FileInfoViewAdmin.class })
  private Date uploadDate;
  private Date modifyDate;
  private String description;
  @JsonView(Views.FileInfoViewAdmin.class)
  private String link;
  @JsonView({ Views.FileInfoView.class, Views.FileInfoViewAdmin.class })
  private int view;
  @JsonView({ Views.FileInfoView.class, Views.FileInfoViewAdmin.class })
  private int likeFile;
  private int repostCount;
  @JsonView({ Views.FileInfoView.class, Views.FileInfoViewAdmin.class })
  private String linkImg;

  @Transient
  @JsonView({ Views.FileInfoView.class, Views.FileInfoViewAdmin.class })
  private int totalDownload;
  
  @Transient
  @JsonView({ Views.FileInfoView.class, Views.FileInfoViewAdmin.class })
  private String userName;

   @Transient
  @JsonView({ Views.FileInfoView.class, Views.FileInfoViewAdmin.class })
  private Long userId;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Users user;

  @JsonManagedReference
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  @JsonView({ Views.FileInfoView.class, Views.FileInfoViewAdmin.class })
  private Category category;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "File_tag", joinColumns = @JoinColumn(name = "File_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
  @JsonView({ Views.FileInfoView.class, Views.FileInfoViewAdmin.class })
  Set<Tag> tags = new HashSet<>();

  @JsonIgnore
  @OneToMany(mappedBy = "file")
  Set<Comment> comments;

  @JsonIgnore
  @OneToMany(mappedBy = "file")
  Set<Download> downloads;

  // @ManyToMany(mappedBy = "likeFiles")
  // private Set<Users> likes = new HashSet<>();

  @JsonIgnore
  @OneToMany(mappedBy = "file")
  Set<Like> likes;

  public File() {
  }

  public File(String fileName, String fileType, Double fileSize, String description, Users user, Category category,
      Set<Tag> tags) {
    this.fileName = fileName;
    this.fileType = fileType;
    this.fileSize = fileSize;
    this.uploadDate = Calendar.getInstance().getTime();
    this.description = description;
    this.user = user;
    this.category = category;
    this.tags = tags;
  }

  public File(String fileName, String fileType, Double fileSize, String description, Users user) {
    this.fileName = fileName;
    this.fileType = fileType;
    this.fileSize = fileSize;
    this.uploadDate = Calendar.getInstance().getTime();
    this.description = description;
    this.user = user;
  }

  public Long getUserId() {
    return this.user.getId();
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return this.user.getName();
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }



  public int getTotalDownload() {
     int totalDownloads = 0;
    for (Download download : downloads) {
        totalDownloads ++;
    }
    return totalDownloads;
  }

  public void setTotalDownload(int total) {
    this.totalDownload = total;
  }



}

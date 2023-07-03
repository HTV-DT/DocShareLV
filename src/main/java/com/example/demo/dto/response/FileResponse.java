package com.example.demo.dto.response;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.demo.model.Category;
import com.example.demo.model.File;
import com.example.demo.model.Like;
import com.example.demo.model.Tag;
import com.example.demo.model.UserFile;
import com.example.demo.model.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileResponse {
    private Long id;
    private String fileName;
    private String fileType;
    private Double fileSize;
    private Date uploadDate;
    private Date modifyDate;
    private String description;
    private String link;
    private int view;
    private int likeFile;
    private int repostCount;
    private String linkImg;
    private Long userId;
    private FriendResponse user;
    private Category category;
    Set<Tag> tags = new HashSet<>();
    boolean like;


    public FileResponse() {
    }


    public FileResponse(Long id, String fileName, String fileType, Double fileSize, Date uploadDate, Date modifyDate, String description, String link, int view, int likeFile, int repostCount, String linkImg, Long userId, FriendResponse user, Category category, Set<Tag> tags) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadDate = uploadDate;
        this.modifyDate = modifyDate;
        this.description = description;
        this.link = link;
        this.view = view;
        this.likeFile = likeFile;
        this.repostCount = repostCount;
        this.linkImg = linkImg;
        this.userId = userId;
        this.user = user;
        this.category = category;
        this.tags = tags;
    }

    public FileResponse(File file,boolean like) {
        this.id = file.getId();
        this.fileName = file.getFileName();
        this.fileType = file.getFileType();
        this.fileSize = file.getFileSize();
        this.uploadDate = file.getUploadDate();
        this.modifyDate = file.getModifyDate();
        this.description = file.getDescription();
        this.link = file.getLink();
        this.view = file.getView();
        this.likeFile = file.getLikeFile();
        this.repostCount = file.getRepostCount();
        this.linkImg = file.getLinkImg();
        this.userId = file.getUserId();
        this.category = file.getCategory();
        this.tags = file.getTags();
        this.like = like;
    }
  


}


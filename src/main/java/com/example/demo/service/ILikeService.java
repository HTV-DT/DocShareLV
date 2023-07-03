package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Like;
import com.example.demo.model.UserFile;

public interface ILikeService {
   
   boolean save(Long userId,Long fileId);
   boolean  deleteLikeById(UserFile id);
   public List<Like> findByFileId(Long fileId);
    public boolean existsByUserIdAndFileId(Long userId, Long fileId);
}

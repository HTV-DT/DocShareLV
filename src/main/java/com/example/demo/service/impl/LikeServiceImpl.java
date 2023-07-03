package com.example.demo.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.File;
import com.example.demo.model.Like;
import com.example.demo.model.UserFile;
import com.example.demo.model.Users;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.IUserRepository;
import com.example.demo.repository.LikeRepository;
import com.example.demo.service.ILikeService;

@Service
public class LikeServiceImpl implements ILikeService {

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    IUserRepository userRepository;

    public boolean save(Long userId,Long fileId) {
        Users user = userRepository.findById(userId).orElse(null);
        File file = fileRepository.findById(fileId).orElse(null);
        if (user != null && file != null) {
            Like like = likeRepository.findByUserIdAndFileId(userId, fileId);
            if (like == null) {
                like = new Like();
                UserFile id = new UserFile(userId, fileId);
                like.setUser(user);
                like.setFile(file);
                like.setCreatedAt(LocalDateTime.now());
                like.setId(id);
                likeRepository.save(like);
                file.setLikeFile(file.getLikeFile() + 1);
                fileRepository.save(file);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteLikeById(UserFile id) {
        Like like = likeRepository.findByUserIdAndFileId(id.getUserId(),id.getFileId());
        
        if (like != null) {
            likeRepository.delete(like);
            
            File file = fileRepository.findById(id.getFileId()).orElse(null);
            if (file != null) {
                file.setLikeFile(file.getLikeFile() - 1);
                fileRepository.save(file);
            }
            
            return true;
        } else {
            return false;
        }
    }
  
   @Override
    public List<Like> findByFileId(Long fileId){
return likeRepository.findByFileId(fileId);
  }

   @Override
    public boolean existsByUserIdAndFileId(Long userId, Long fileId){
return likeRepository.existsByUserIdAndFileId(userId, fileId);
   }
 }


    

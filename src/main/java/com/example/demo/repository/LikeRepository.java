package com.example.demo.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.File;
import com.example.demo.model.Like;
import com.example.demo.model.UserFile;
import com.example.demo.model.Users;

@Repository
public interface LikeRepository extends JpaRepository<Like,UserFile> {
    @Transactional
    void deleteByUserIdAndFileId(Long userId, Long fileId);
    Like findByUserIdAndFileId(Long userId, Long fileId);
    List<Like> findByFileId(Long fileId);
      boolean existsByUserIdAndFileId(Long userId, Long fileId);
}   

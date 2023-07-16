package com.example.demo.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.response.CommentResponse;
import com.example.demo.model.Comment;
import com.example.demo.model.Download;
import com.example.demo.model.File;
import com.example.demo.model.Users;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.DownloadRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.IUserRepository;
import com.example.demo.service.CommentService;
import com.example.demo.service.DownloadService;

@Service
public class DownloadServiceImpl implements DownloadService{
 
    @Autowired
    private DownloadRepository downloadRepository;
    
    @Autowired
    FileRepository fileRepository;

    @Autowired
    IUserRepository userRepository;
 
    @Override
    public Boolean saveDownload(Long userId, Long fileId) {
         Users user = userRepository.findById(userId).orElse(null);
        File file = fileRepository.findById(fileId).orElse(null);
        if (user != null && file != null) {
            Download download = new Download(file,user,LocalDateTime.now());
            downloadRepository.save(download);
            return true;
        }else{
            return false;
        }
    }
   
    @Override
    public Long getTotalDownloadsForSystem() {
        return downloadRepository.getTotalDownloadsForSystem();
    }
}
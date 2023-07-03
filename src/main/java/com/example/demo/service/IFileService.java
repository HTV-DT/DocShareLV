package com.example.demo.service;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Category;
import com.example.demo.model.File;
import com.example.demo.model.Tag;
import com.example.demo.model.Users;
import com.google.api.services.drive.model.User;

public interface IFileService {
    String uploadFile(MultipartFile file, String filePath, boolean isPublic);
    List<Category> getAllFileCategories();
    List<File> getAllFiles();
    List<File> search(String keyword);
    void  deleteFileById(Long id);
    String deleteFile(String id,Long id_file,Users users,boolean actor) throws Exception;
    void downloadFile(String id, OutputStream outputStream) throws IOException, GeneralSecurityException;
    Optional<File> findById(Long id); 
    List<File> getTopFile();
    List<File> getViewFile();
    Double sumView();	
     public List<File> searchFilesByTag(File selectedFile);

     public List<File> searchFilesByCategory(Long id);
}

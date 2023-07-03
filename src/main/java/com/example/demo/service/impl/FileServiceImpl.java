package com.example.demo.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Category;
import com.example.demo.model.File;
import com.example.demo.model.Tag;
import com.example.demo.model.Users;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.service.IFileService;

@Service
public class FileServiceImpl implements IFileService {
    @Autowired
    FileRepository fileRepository;

    @Autowired
    GoogleFileManager googleFileManager;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    CategoryRepository categoryRepository;

    public File save(File File) {
        return fileRepository.save(File);
    }

    public Category findByCategoryName(String name) {
        return categoryRepository.findByCategoryName(name);
    }

    @Override
    public List<Category> getAllFileCategories() {
        return categoryRepository.findAll();
    }

    // @Override
    // public void uploadFile(MultipartFile file, String filePath, boolean
    // isPublic,String Title,String description,Category category,Set<Tag> tags) {
    // String type = "";
    // String role = "";
    // if (isPublic) {
    // // Public file of folder for everyone
    // type = "anyone";
    // role = "reader";
    // } else {
    // // Private
    // type = "private";
    // role = "private";
    // }
    // Users user= userServiceImpl.findByUsername(filePath).orElse(null);
    // File File = new File(file.getOriginalFilename(), file.getContentType(),
    // file.getSize()/1024,filePath,user,category,tags);
    // save(File);
    // googleFileManager.uploadFile(file,filePath, type, role);
    // }

    @Override
    public String uploadFile(MultipartFile file, String filePath, boolean isPublic) {
        String type = "";
        String role = "";
        if (isPublic) {
            // Public file of folder for everyone
            type = "anyone";
            role = "reader";
        } else {
            // Private
            type = "private";
            role = "private";
        }

        String fileId = googleFileManager.uploadFile(file, filePath, type, role);
        String fileLink = fileId;
        return fileLink;
    }

    @Override
    public List<File> getAllFiles() {
        return fileRepository.findAll();
    }

    @Override
    public List<File> search(String keyword) {
        if (keyword != null) {
            return fileRepository.search(keyword);
        }
        return fileRepository.findAll();
    }

    @Override
    public void deleteFileById(Long id) {
        fileRepository.deleteById(id);
    }

    @Override
    public String deleteFile(String id, Long id_file, Users user, boolean actor) throws Exception {

        Optional<File> optionalFile = fileRepository.findById(id_file);
        if (!optionalFile.isPresent()) {
            throw new NotFoundException("File not found");
        }

        File file = optionalFile.get();

        if (file != null) {
            if (file.getUser().getId() == user.getId()) {
                if (file.getLink().equals(id)) {
                   
                        googleFileManager.deleteFileOrFolder(id);
                        fileRepository.deleteById(id_file);
                        user.setMaxUpload(user.getMaxUpload() + file.getFileSize());
                        userServiceImpl.save(user);
                        if (actor == true) {
                            userServiceImpl.sendDelete(user, file);
                        }
                        return "File deleted successfully.";
                  
                } else {
                    throw new NotFoundException("File not found");
                }
            } else {
                throw new NotFoundException(
                        "The post cannot be deleted. Only the author of the post has the permission to delete it.");
            }
        } else {
            throw new NotFoundException("File not found");
        }

    }

    @Override
    public void downloadFile(String id, OutputStream outputStream) throws IOException, GeneralSecurityException {
        googleFileManager.downloadFile(id, outputStream);
    }

    @Override
    public Optional<File> findById(Long id) {
        return fileRepository.findById(id);
    }

    @Override
    public List<File> getTopFile() {
        return fileRepository.listTopFile();
    }

    @Override
    public List<File> getViewFile() {
        return fileRepository.listViewsFile();
    }

    @Override
    public Double sumView() {
        return fileRepository.sumView();
    }

    @Override
    public List<File> searchFilesByTag(File selectedFile) {
        // get the tags of the selected file
        Set<Tag> tags = selectedFile.getTags();
        // find all files that have at least one common tag with the selected file
          return fileRepository.findByTagsIn(tags, PageRequest.of(0, 20));
    }

    @Override
    public List<File> searchFilesByCategory(Long id) {
return fileRepository.findFilesByCategoryId(id, PageRequest.of(0, 20));
    }
}
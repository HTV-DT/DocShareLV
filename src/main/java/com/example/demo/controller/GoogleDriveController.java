package com.example.demo.controller;

import com.example.demo.dto.request.CommentForm;
import com.example.demo.dto.request.FileForm;
import com.example.demo.dto.request.FollowForm;
import com.example.demo.dto.response.CommentResponse;
import com.example.demo.dto.response.FileResponse;
import com.example.demo.model.Access;
import com.example.demo.model.Category;
import com.example.demo.model.Comment;
import com.example.demo.model.File;
import com.example.demo.model.Like;
import com.example.demo.model.Role;
import com.example.demo.model.RoleName;
import com.example.demo.model.UserFile;
import com.example.demo.model.Tag;
import com.example.demo.model.Users;
import com.example.demo.service.AccessService;
import com.example.demo.service.CommentService;
import com.example.demo.service.DownloadService;
import com.example.demo.service.ILikeService;
import com.example.demo.service.IRepostService;
import com.example.demo.service.impl.FileServiceImpl;
import com.example.demo.service.impl.LikeServiceImpl;
import com.example.demo.service.impl.RoleServiceImpl;
import com.example.demo.service.impl.TagServiceImpl;
import com.example.demo.service.impl.UserServiceImpl;
import com.example.demo.utils.ConvertByteToMB;
import com.example.demo.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;

import javassist.NotFoundException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.Console;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/file")
@CrossOrigin("*")
@RestController(value = "googleDriveController")
public class GoogleDriveController {

    @Autowired
    FileServiceImpl fileService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    TagServiceImpl tagServiceImpl;

    @Autowired
    LikeServiceImpl likeService;

    @Autowired
    IRepostService repostService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private DownloadService downloadService;

    @Autowired
    AccessService accessService;

    @Autowired
    RoleServiceImpl roleService;

    private ConvertByteToMB convertByteToMB;

    // Upload file to public
    @PostMapping(value = "/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> uploadFile(@RequestParam("fileUpload") MultipartFile fileUpload,
            @RequestParam("shared") String shared,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("tags") Set<String> tagNames,
            @RequestParam("iduser") Long idUser,
            @RequestParam("fileImg") MultipartFile fileImg) {
        String error = "";
        if (fileUpload == null || fileUpload.isEmpty()) {
            error += "Tham số 'fileUpload' không được rỗng. ";
        }
        if (shared == null || shared.trim().isEmpty()) {
            error += "Giá trị của tham số 'shared' không được rỗng. ";
        }
        if (title == null || title.trim().isEmpty()) {
            error += "Giá trị của tham số 'title' không được rỗng. ";
        }
        if (description == null || description.trim().isEmpty()) {
            error += "Giá trị của tham số 'description' không được rỗng. ";
        }
        if (category == null || category.trim().isEmpty()) {
            error += "Giá trị của tham số 'category' không được rỗng. ";
        }
        if (tagNames == null || tagNames.isEmpty()) {
            error += "Giá trị của tham số 'tagNames' không được rỗng. ";
        }
        if (idUser == null) {
            error += "Giá trị của tham số 'idUser' không được rỗng. ";
        }
        if (fileImg == null || fileImg.isEmpty()) {
            error += "Tham số 'fileImg' không được rỗng. ";
        }

        // Nếu có lỗi, trả về phản hồi lỗi
        if (!error.isEmpty()) {
            return ResponseEntity.badRequest().body(error.trim());
        }

        Users user = userService.findById(idUser).orElse(null);
        if (user.getUsername().equals("")) {
            user.setUsername("Root");// Save to default folder if the user does not select a folder to save - you
                                     // canchange it
        }

        double kb = fileUpload.getSize() / 1024;
        double mb = kb / 1024;
        double roundedMb = (double) Math.round(mb * 1000) / 1000;

        user.setMaxUpload(user.getMaxUpload() - roundedMb);

        if (user.getMaxUpload() < 0) {
            return ResponseEntity.badRequest().body("Upload failed: storage limit exceeded.");
        }

        Set<Tag> tags = tagNames.stream()
                .map(TagName -> {
                    Optional<Tag> optionalTag = tagServiceImpl.findByTagName(TagName);
                    Tag tag = optionalTag.orElseGet(() -> {
                        Tag newTag = new Tag(TagName);
                        tagServiceImpl.save(newTag);
                        return newTag;
                    });
                    return tag;
                })
                .collect(Collectors.toSet());

        Category categoryName = fileService.findByCategoryName(category);
        if (Objects.isNull(categoryName) || categoryName.getCategoryName().isEmpty()) {

            return ResponseEntity.badRequest().body("Category not found or null");
        }
        File file = new File(title, fileUpload.getContentType(),
                roundedMb, description, user, categoryName, tags);
        String link = fileService.uploadFile(fileUpload, user.getUsername(), Boolean.parseBoolean(shared));
        String linkImg = fileService.uploadFile(fileImg, user.getUsername(), true);
        // PDDocument document;

        // try {
        // document = PDDocument.load(fileUpload.getInputStream());
        // int page = document.getNumberOfPages();
        // file.setView(page);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        userService.save(user);

        file.setLink(link);
        file.setLinkImg(linkImg);
        fileService.save(file);

        return ResponseEntity.ok(file);
    }

    // Delete file by id thêm xóa id @RequestBody
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestBody FileForm fileForm, HttpServletRequest request)
            throws Exception {
        Optional<Users> optionalUser = userService.findById(fileForm.getUser_id());

        if (!optionalUser.isPresent()) {
            throw new NotFoundException("User not found with id: " + fileForm.getUser_id());
        }
        Users user = optionalUser.get();
        fileService.deleteFile(fileForm.getDrive_id(), fileForm.getFile_id(), user, true);
        return new ResponseEntity<>("delete successfully", HttpStatus.OK);
    }

    @DeleteMapping("/delete/user")
    public ResponseEntity<String> deleteFileUser(@RequestBody FileForm fileForm, HttpServletRequest request)
            throws Exception {
        Users user = userService.findById(fileForm.getUser_id()).orElse(null);
        if (user.getUsername().equals("")) {
            user.setUsername("Root");// Save to default folder if the user does not select a folder to save - you
                                     // canchange it
        }
        fileService.deleteFile(fileForm.getDrive_id(), fileForm.getFile_id(), user, false);
        return new ResponseEntity<>("delete successfully", HttpStatus.OK);

    }

    @GetMapping("/review/{id}")
    public void reviewFile(@PathVariable String id, HttpServletResponse response)
            throws IOException, GeneralSecurityException {
        fileService.downloadFile(id, response.getOutputStream());
    }

    // Download file
    @GetMapping("/download/{id}/{user_id}/{file_id}")
    public ResponseEntity<?> downloadFile(@PathVariable String id, @PathVariable Long user_id,
            @PathVariable Long file_id, HttpServletResponse response) throws IOException, GeneralSecurityException {
        Users user = userService.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Access> accesses = accessService.findAccessByUserAndValid(user_id, user);
        if (accesses == null || accesses.isEmpty()) {

            Set<Role> roles = user.getRoles();
            Role userRole = roleService.findByName(RoleName.USER)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            roles.remove(userRole);
            user.setRoles(roles);
            userService.save(user);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new HashMap<String, Object>() {
                {
                    put("status", false);
                    put("message", user.getRoles());
                }
            });
        }
        Access accessWithPackageId2 = accesses.stream()
                .filter(access -> access.getPackages().getId() == 2)
                .findFirst()
                .orElse(null);
        if (accessWithPackageId2 != null) {
            fileService.downloadFile(id, response.getOutputStream());
            downloadService.saveDownload(user_id, file_id);
            return ResponseEntity.ok().build();
        }

        Access closestCreatedAtAccess = accesses.stream()
                .sorted(Comparator.comparing(Access::getCreatedAt).reversed())
                .findFirst()
                .orElse(null);
        if (closestCreatedAtAccess != null) {
            closestCreatedAtAccess.setNumOfAccess(closestCreatedAtAccess.getNumOfAccess() - 1);
            accessService.save(closestCreatedAtAccess);
            fileService.downloadFile(id, response.getOutputStream());
            downloadService.saveDownload(user_id, file_id);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new HashMap<String, Object>() {
            {
                put("status", false);
                put("message", "No valid access found for this user.");
            }
        });
    }

    @PutMapping("update")
    public ResponseEntity<File> updateFile(@RequestBody FileResponse fileResponse) {
        Optional<File> optionalFile = fileService.findById(fileResponse.getId());
        if (!optionalFile.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        File file = optionalFile.get();
        file.setFileName(fileResponse.getFileName());
        // LocalDateTime localDateTime = LocalDateTime.now();
        // ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        // Instant instant = zonedDateTime.toInstant();
        Date now = new Date();
        file.setModifyDate(now);
        file.setDescription(fileResponse.getDescription());
        File savedFile = fileService.save(file);
        return ResponseEntity.ok(savedFile);
    }

    @PostMapping("/search/tagname")
    public ResponseEntity<?> Search(@RequestParam("tagName") String tagName) {
        List<Tag> tags = tagServiceImpl.search(tagName);
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    @GetMapping("/category/list")
    public ResponseEntity<List<Category>> getAllPackages() {
        try {
            List<Category> categories = fileService.getAllFileCategories();
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/ListFiles")
    @JsonView(Views.FileInfoView.class)
    public ResponseEntity<List<File>> getFiles() {
        try {
            List<File> files = fileService.getAllFiles();
            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list/tag")
    @JsonView(Views.FileInfoView.class)
    public ResponseEntity<List<File>> getFileTag(@RequestParam("file_id") Long file_id) {
        try {
            Optional<File> optionalFile = fileService.findById(file_id);
            if (!optionalFile.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            File file = optionalFile.get();
            List<File> files = fileService.searchFilesByTag(file);
            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

       @GetMapping("/list/category")
    @JsonView(Views.FileInfoView.class)
    public ResponseEntity<List<File>> getFileCategory(@RequestParam("id") Long id) {
        try {
            List<File> files = fileService.searchFilesByCategory(id);
            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/TopFiles")
    @JsonView(Views.FileInfoView.class)
    public ResponseEntity<List<File>> getTopFiles() {
        try {
            List<File> files = fileService.getTopFile();
            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/Featured")
    @JsonView(Views.FileInfoView.class)
    public ResponseEntity<List<File>> getFeaturedFiles() {
        try {
            List<File> files = fileService.getViewFile();
            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<File>> searchFile(@RequestParam("tagName") String tagName) {
        List<File> files = fileService.search(tagName);
        return ResponseEntity.ok(files);
    }

    @PostMapping("/like")
    public ResponseEntity<Boolean> likeFile(@RequestBody CommentForm likeForm) {
        boolean result = likeService.save(likeForm.getUserid(), likeForm.getFileid());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<List<CommentResponse>> getCommentsByFileId(@PathVariable("id") Long fileId) {
        List<CommentResponse> commentResponses = commentService.getCommentsByFileId(fileId);
        return ResponseEntity.ok(commentResponses);
    }

    @PostMapping("/comments")
    public ResponseEntity<Boolean> saveComment(@RequestBody CommentForm commentForm) {
        try {
            boolean kq = commentService.saveComment(commentForm.getUserid(), commentForm.getFileid(),
                    commentForm.getContten());
            return new ResponseEntity<>(kq, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // @GetMapping("/getFile/id")
    // public ResponseEntity<FileResponse> getFile(@RequestParam("file_id") Long file_id,@RequestParam("user_id") Long user_id) {
    //     Optional<File> optionalFile = fileService.findById(file_id);
    //     File file = optionalFile
    //             .orElseThrow(() -> new org.springframework.security.acls.model.NotFoundException("File not found"));
    //     file.setView(file.getView() + 1);
    //     fileService.save(file);
    //     FileResponse fileResponse= new FileResponse(file,likeService.existsByUserIdAndFileId(user_id,file_id));
    //     return new ResponseEntity<>(fileResponse, HttpStatus.OK);
    // }

      @GetMapping("/getFile/id")
    public ResponseEntity<File> getFile(@RequestParam("file_id") Long file_id) {
        Optional<File> optionalFile = fileService.findById(file_id);
        File file = optionalFile
                .orElseThrow(() -> new org.springframework.security.acls.model.NotFoundException("File not found"));
        file.setView(file.getView() + 1);
        fileService.save(file);
        return new ResponseEntity<>(file, HttpStatus.OK);
    }

    @DeleteMapping("/delete/like")
    public ResponseEntity<Boolean> deleteLike(@RequestBody CommentForm commentForm) {
        try {
            UserFile userFile = new UserFile(commentForm.getFileid(), commentForm.getUserid());
            boolean kq = likeService.deleteLikeById(userFile);
            return new ResponseEntity<>(kq, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/repost")
    public ResponseEntity<Boolean> saveRepost(@RequestBody CommentForm repostForm) {
        try {
            boolean kq = repostService.save(repostForm.getUserid(), repostForm.getFileid(), repostForm.getContten());
            return new ResponseEntity<>(kq, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
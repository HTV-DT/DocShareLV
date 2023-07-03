package com.example.demo.controller;

import com.example.demo.dto.request.FollowForm;
import com.example.demo.dto.request.SignInForm;
import com.example.demo.dto.request.SignUpForm;
import com.example.demo.dto.request.UserForm;
import com.example.demo.dto.response.FriendResponse;
import com.example.demo.dto.response.JwtResponse;
import com.example.demo.dto.response.ResponseMessage;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.model.Order;
import com.example.demo.model.Role;
import com.example.demo.model.RoleName;
import com.example.demo.model.Users;
import com.example.demo.security.jwt.JwtProvider;
import com.example.demo.security.userpincal.UserPrinciple;
import com.example.demo.service.OrderService;
import com.example.demo.service.impl.FileServiceImpl;
import com.example.demo.service.impl.RoleServiceImpl;
import com.example.demo.service.impl.UserServiceImpl;
import com.mysql.cj.result.Field;

import net.bytebuddy.utility.RandomString;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    @Autowired
    UserServiceImpl userService;
    @Autowired
    RoleServiceImpl roleService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    FileServiceImpl fileService;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @RequestBody SignUpForm signUpForm, HttpServletRequest request)
            throws MessagingException, UnsupportedEncodingException {
        if (userService.existsByUsername(signUpForm.getUsername())) {
            return new ResponseEntity<>(new ResponseMessage("The username is existed"), HttpStatus.OK);
        }
        if (userService.existsByEmail(signUpForm.getEmail())) {
            return new ResponseEntity<>(new ResponseMessage("The email is existed"), HttpStatus.OK);
        }
        Users users = new Users(signUpForm.getName(), signUpForm.getUsername(), signUpForm.getEmail(),
                passwordEncoder.encode(signUpForm.getPassword()));

        users.setMaxUpload((double) 2048);

        users.setAvatar("https://thuvienlogo.com/data/01/logo-con-gau-08.jpg");

        String randomCode = RandomString.make(64);
        users.setVerificationCode(randomCode);
        users.setEnabled(false);

        userService.register(users, getSiteURL(request));
        return new ResponseEntity<>(new ResponseMessage("Create success!"), HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody SignInForm signInForm) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInForm.getUsername(), signInForm.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.createToken(authentication);
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();

        return ResponseEntity.ok(
                new JwtResponse(token, userPrinciple.getName(), userPrinciple.getAuthorities(), userPrinciple.getId()));
    }

    @PutMapping("/update/profile")
    public ResponseEntity<Users> updateUser(@RequestParam("fileImg") MultipartFile fileImg,
            @RequestParam("user_id") Long user_id,
            @RequestParam("name") String name,
            @RequestParam("password") String password) {
        Optional<Users> optionalUser = userService.findById(user_id);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Users user = optionalUser.get();
        user.setName(name);
        user.setPassword(password);
        String linkImg = fileService.uploadFile(fileImg, user.getUsername(), true);
        user.setAvatar(linkImg);
        Users savedUser = userService.save(user);
        return ResponseEntity.ok(savedUser);
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @GetMapping("/verify")
    public void verifyUser(@Param("code") String code, HttpServletResponse response) throws IOException {
        if (userService.verify(code)) {
            // return "redirect:/http://localhost:3000/login"; kiểu String
            response.sendRedirect("http://localhost:3000/login");
        } else {
            response.sendRedirect("http://localhost:3000/login");
        }
    }

    @PostMapping("/follow")
    public ResponseEntity<?> follow(@RequestBody FollowForm followForm, HttpServletRequest request)
            throws MessagingException, UnsupportedEncodingException {

        Optional<Users> optionalFriend = userService.findById(followForm.getFriend_id());
        Optional<Users> optionalUser = userService.findById(followForm.getUser_id());
        Users user = optionalUser.orElseThrow(() -> new NotFoundException("User not found"));
        Users friend = optionalFriend.orElseThrow(() -> new NotFoundException("Friend not found"));

        if (user.getFriends().contains(friend)) {
            throw new IllegalStateException("User is already following this friend");
        }

        if (friend.getId().equals(user.getId())) {
            throw new IllegalArgumentException("User cannot follow themselves");
        }

        if (!user.isEnabled() || !friend.isEnabled()) {
            throw new IllegalStateException("Both user and friend must be verified before following");
        }

        user.getFriends().add(friend);
        userService.save(user);

        return new ResponseEntity<>(new ResponseMessage("Create success!"), HttpStatus.OK);
    }

    @DeleteMapping("/unfollow")
    public ResponseEntity<ResponseMessage> deleteUnfollow(@RequestParam("user_id") Long userId,
            @RequestParam("friend_id") Long friendId) {
        // Tìm người dùng và bạn bè theo ID
        Optional<Users> optionalUser = userService.findById(userId);
        Optional<Users> optionalFriend = userService.findById(friendId);

        // Kiểm tra xem người dùng và bạn bè có tồn tại hay không
        if (!optionalUser.isPresent()) {
            throw new NotFoundException("User not found");
        }
        if (!optionalFriend.isPresent()) {
            throw new NotFoundException("Friend not found");
        }

        // Lấy người dùng và bạn bè từ Optional
        Users user = optionalUser.get();
        Users friend = optionalFriend.get();

        // Xóa bạn bè khỏi danh sách bạn bè của người dùng
        Set<Users> friends = user.getFriends();
        if (friends.contains(friend)) {
            friends.remove(friend);
            userService.save(user);
        } else {
            throw new IllegalArgumentException("Friend not found in user's friend list");
        }

        return ResponseEntity.ok(new ResponseMessage("Unfollowed successfully"));
    }

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = userService.getAllUser();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // @GetMapping("/user")
    // public ResponseEntity<?> getUser(@RequestParam("user_id") Long user_id,
    // @RequestParam("fiend_id") Long friend_id) {
    // Users user = userService.findById(user_id)
    // .orElseThrow(() -> new NotFoundException("User not found"));
    // Users friendUsers = userService.findById(friend_id)
    // .orElseThrow(() -> new NotFoundException("User not found"));
    // List<FriendResponse> friendDTOs = new ArrayList<>();
    // for (Users friend : user.getFriends()) {
    // friendDTOs.add(new FriendResponse(friend));
    // }

    // List<FriendResponse> followingDTOs = new ArrayList<>();

    // for (Users following : userService.getFollowing(user_id)) {
    // followingDTOs.add(new FriendResponse(following));
    // }
    // boolean hasFriend = friendUsers.hasFriendWithId(user_id);

    // return new ResponseEntity<>(new UserResponse(user.getId(),
    // user.getUsername(),
    // user.getAvatar(), user.getFiles(), friendDTOs, hasFriend), HttpStatus.OK);
    // }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestParam("user_id") Long user_id) {
        Users user = userService.findById(user_id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<FriendResponse> friendDTOs = new ArrayList<>();
        for (Users friend : user.getFriends()) {
            friendDTOs.add(new FriendResponse(friend));
        }

        List<FriendResponse> followingDTOs = new ArrayList<>();

        for (Users following : userService.getFollowing(user_id)) {
            followingDTOs.add(new FriendResponse(following));
        }

        return new ResponseEntity<>(new UserResponse(user.getId(),
                user.getUsername(),
                user.getAvatar(), user.getFiles(), friendDTOs, followingDTOs),
                HttpStatus.OK);
    }

    @PutMapping("/active")
    public ResponseEntity<?> activeUser(@RequestBody UserForm userForm, HttpServletRequest request)
            throws MessagingException, UnsupportedEncodingException {
        Users user = userService.findById(userForm.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setEnabled(userForm.isEnabled());
        userService.save(user);

        if (userForm.isEnabled() == false) {
            userService.sendActive(user);
        }
        return new ResponseEntity<>(new ResponseMessage("User enabled!"), HttpStatus.OK);
    }

    // @GetMapping("/following")
    // public ResponseEntity<List<Object[]>> gettoatlPrice(@RequestBody UserForm
    // userForm) {
    // Long user_id=userForm.getId();
    // List<Object[]> list = userService.following(user_id);
    // return ResponseEntity.ok(list);
    // }
}

package com.example.demo.service;

import com.example.demo.model.File;
import com.example.demo.model.Users;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.data.repository.query.Param;

public interface IUserService {
    Optional<Users> findByUsername(String name); // Tim kiem User co ton tai trong DB khong?

    Optional<Users> findById(Long id);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Users save(Users users);

    Boolean verify(String verificationCode, String password);

    void register(Users user, String siteURL) throws UnsupportedEncodingException, MessagingException;

    List<Users> getAllUser();

    List<Users> getFollowing(Long user_id);

    public long countUsers();

    public Long getUserCount();

    public Optional<Users> findByEmail(String email);

    public void sendActive(Users user) throws UnsupportedEncodingException, MessagingException;

    public void sendDelete(Users user, File file) throws UnsupportedEncodingException, MessagingException;
    // public List<Object[]> following(@Param("user_id") Long user_id);
}

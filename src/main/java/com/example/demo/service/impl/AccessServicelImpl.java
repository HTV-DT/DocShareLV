package com.example.demo.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Access;
import com.example.demo.model.Role;
import com.example.demo.model.RoleName;
import com.example.demo.model.Users;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.IRoleRepository;
import com.example.demo.repository.IUserRepository;
import com.example.demo.service.AccessService;
import com.example.demo.service.IRoleService;

import javassist.NotFoundException;

@Service
public class AccessServicelImpl implements AccessService {
    @Autowired
    AccessRepository accessRepository;

    @Autowired
    IUserRepository userRepository;

    @Override
    public Access save(Access access) {
        return accessRepository.save(access);
    }

    public List<Access> findAccessByUserAndValid(Long userId, Users user) {
        LocalDateTime now = LocalDateTime.now();
        List<Access> accessList = accessRepository.findByUser(user);
        List<Access> validAccessList = new ArrayList<>();

        for (Access access : accessList) {

            if (now.isBefore(access.getCreatedAt())) {
                if (access.getPackages().getId() == 2) {
                    validAccessList.add(access);
                } else if (access.getNumOfAccess() > 0) {
                    validAccessList.add(access);
                } else {
                    accessRepository.delete(access);
                }

            } else {
                accessRepository.delete(access);
            }
        }
        return validAccessList;
    }

    @Override
    public List<Access> getAccessByFileId(Long userId) {
        return accessRepository.findByUserId(userId);
    }

}
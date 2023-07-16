package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.File;
import com.example.demo.model.Package;
import com.example.demo.model.Users;

public interface PackageService {
    List<Package> getAllPackages();
    Optional<Package> findById(Long id); 
    Package save(Package package1);
     public List<Package> getAllActivePackages();
     public List<Package> findPackagesByType();
     public boolean hasActivePackageWithType();
     public Package getActivePackageWithType();
}

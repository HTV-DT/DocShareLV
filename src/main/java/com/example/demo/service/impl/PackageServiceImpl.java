package com.example.demo.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Package;
import com.example.demo.repository.PackageRepository;
import com.example.demo.service.PackageService;

@Service
public class PackageServiceImpl implements PackageService {

    @Autowired
    PackageRepository packageRepository;

    @Override
    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    @Override
    public Optional<Package> findById(Long id) {
        return packageRepository.findById(id);
    }

    @Override
    public Package save(Package package1) {
        return packageRepository.save(package1);
    }

     @Override
    public List<Package> getAllActivePackages() {
        return packageRepository.findByActiveTrue();
    }

  
 @Override
 public List<Package> findPackagesByType() {
    return packageRepository.findByType();
}

 @Override
public boolean hasActivePackageWithType() {
    return packageRepository.existsByActiveAndType(true, 2);
}
 @Override
public Package getActivePackageWithType() {
    Optional<Package> optionalPackage = packageRepository.findByActiveAndType(true, 2);
    return optionalPackage.orElse(null);
}
}

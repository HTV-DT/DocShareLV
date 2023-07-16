package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Package;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {
     List<Package> findByActiveTrue();
     @Query("SELECT p FROM Package p WHERE p.type = 2")
     List<Package> findByType();

     boolean existsByActiveAndType(boolean active, int type);
          Optional<Package> findByActiveAndType(boolean active, int type);
    }   

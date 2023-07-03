package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Tag;


@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByTagName(String tagName);
    @Query(value = "SELECT * FROM tag WHERE CONCAT(tag.tag_name) LIKE %?1%", nativeQuery=true)
    List<Tag> search(String keyword);
}   
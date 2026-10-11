package com.coffeekartel.repository;

import com.coffeekartel.entity.Category;
import com.coffeekartel.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByIsAvailableTrueOrderByCreatedAtDesc();
    List<MenuItem> findByCategoryAndIsAvailableTrue(Category category);
    Optional<MenuItem> findBySlug(String slug);
}

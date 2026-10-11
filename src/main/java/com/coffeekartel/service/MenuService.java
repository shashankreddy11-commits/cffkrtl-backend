package com.coffeekartel.service;

import com.coffeekartel.dto.request.CreateMenuItemRequest;
import com.coffeekartel.dto.request.UpdateMenuItemRequest;
import com.coffeekartel.dto.response.CategoryResponse;
import com.coffeekartel.dto.response.MenuItemResponse;
import com.coffeekartel.entity.Category;
import com.coffeekartel.entity.MenuItem;
import com.coffeekartel.exception.ResourceNotFoundException;
import com.coffeekartel.repository.CategoryRepository;
import com.coffeekartel.repository.MenuItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private static final Logger log = LoggerFactory.getLogger(MenuService.class);

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;

    public MenuService(MenuItemRepository menuItemRepository, CategoryRepository categoryRepository) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(this::mapCategoryToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> getAllActiveMenuItems() {
        return menuItemRepository.findByIsAvailableTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapMenuItemToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MenuItemResponse> getAllMenuItemsAdmin() {
        return menuItemRepository.findAll()
                .stream()
                .map(this::mapMenuItemToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MenuItemResponse getMenuItemBySlug(String slug) {
        MenuItem item = menuItemRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with slug: " + slug));
        return mapMenuItemToResponse(item);
    }

    @Transactional(readOnly = true)
    public MenuItemResponse getMenuItemById(Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
        return mapMenuItemToResponse(item);
    }

    @Transactional
    public MenuItemResponse createMenuItem(CreateMenuItemRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        MenuItem item = new MenuItem(
                category,
                request.getSlug(),
                request.getName(),
                request.getSubCategory(),
                request.getDescription(),
                request.getPrice(),
                request.isSignature(),
                request.isPopular(),
                request.getDietaryType()
        );
        item.setImageUrl(request.getImageUrl());

        MenuItem saved = menuItemRepository.save(item);
        log.info("Created menu item '{}' in category '{}'", saved.getName(), category.getName());
        return mapMenuItemToResponse(saved);
    }

    @Transactional
    public MenuItemResponse updateMenuItem(Long id, UpdateMenuItemRequest request) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        item.setCategory(category);
        item.setName(request.getName());
        item.setSubCategory(request.getSubCategory());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setAvailable(request.isAvailable());
        item.setSignature(request.isSignature());
        item.setPopular(request.isPopular());
        item.setDietaryType(request.getDietaryType());
        item.setImageUrl(request.getImageUrl());

        MenuItem saved = menuItemRepository.save(item);
        log.info("Updated menu item '{}'", saved.getName());
        return mapMenuItemToResponse(saved);
    }

    @Transactional
    public MenuItemResponse toggleAvailability(Long id, boolean available) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        item.setAvailable(available);
        MenuItem saved = menuItemRepository.save(item);
        log.info("Menu item '{}' availability set to {}", saved.getName(), available);
        return mapMenuItemToResponse(saved);
    }

    @Transactional
    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Menu item not found with id: " + id);
        }
        menuItemRepository.deleteById(id);
        log.info("Deleted menu item with id {}", id);
    }

    public CategoryResponse mapCategoryToResponse(Category c) {
        return new CategoryResponse(
                c.getId(),
                c.getName(),
                c.getSlug(),
                c.getDisplayOrder(),
                c.isActive()
        );
    }

    public MenuItemResponse mapMenuItemToResponse(MenuItem m) {
        String priceDisplay = m.getPrice() != null ? "₹" + m.getPrice().stripTrailingZeros().toPlainString() : "Price at Counter";
        return new MenuItemResponse(
                m.getId(),
                m.getSlug(),
                m.getName(),
                m.getCategory() != null ? m.getCategory().getId() : null,
                m.getCategory() != null ? m.getCategory().getSlug() : null,
                m.getCategory() != null ? m.getCategory().getName() : null,
                m.getSubCategory(),
                m.getDescription(),
                m.getPrice(),
                priceDisplay,
                m.isAvailable(),
                m.isSignature(),
                m.isPopular(),
                m.getDietaryType(),
                m.getImageUrl(),
                m.getCreatedAt()
        );
    }
}

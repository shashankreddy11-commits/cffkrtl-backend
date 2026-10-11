package com.coffeekartel.controller;

import com.coffeekartel.dto.request.CreateMenuItemRequest;
import com.coffeekartel.dto.request.UpdateMenuItemRequest;
import com.coffeekartel.dto.response.ApiResponse;
import com.coffeekartel.dto.response.CategoryResponse;
import com.coffeekartel.dto.response.MenuItemResponse;
import com.coffeekartel.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Menu & Categories", description = "Endpoints for browsing café menu items and categories")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/categories")
    @Operation(summary = "Get menu categories", description = "Retrieve list of all active menu categories")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(menuService.getAllActiveCategories());
    }

    @GetMapping("/menu")
    @Operation(summary = "Get menu items", description = "Retrieve list of all available menu items")
    public ResponseEntity<List<MenuItemResponse>> getMenu() {
        return ResponseEntity.ok(menuService.getAllActiveMenuItems());
    }

    @GetMapping("/menu/{slug}")
    @Operation(summary = "Get menu item by slug", description = "Retrieve menu item details by item slug")
    public ResponseEntity<MenuItemResponse> getMenuItem(@PathVariable String slug) {
        return ResponseEntity.ok(menuService.getMenuItemBySlug(slug));
    }

    @GetMapping("/admin/menu")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all menu items (Admin)", description = "Retrieve all menu items including unavailable items")
    public ResponseEntity<List<MenuItemResponse>> getAllMenuItemsAdmin() {
        return ResponseEntity.ok(menuService.getAllMenuItemsAdmin());
    }

    @PostMapping("/admin/menu")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create menu item (Admin)", description = "Add a new menu item to the café catalog")
    public ResponseEntity<MenuItemResponse> createMenuItem(@Valid @RequestBody CreateMenuItemRequest request) {
        MenuItemResponse created = menuService.createMenuItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/admin/menu/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update menu item (Admin)", description = "Modify details of an existing menu item")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMenuItemRequest request) {
        return ResponseEntity.ok(menuService.updateMenuItem(id, request));
    }

    @PatchMapping("/admin/menu/{id}/availability")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle menu item availability (Admin)", description = "Quickly mark an item available or out of stock")
    public ResponseEntity<MenuItemResponse> toggleAvailability(
            @PathVariable Long id,
            @RequestParam boolean available) {
        return ResponseEntity.ok(menuService.toggleAvailability(id, available));
    }

    @DeleteMapping("/admin/menu/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete menu item (Admin)", description = "Remove a menu item from the catalog")
    public ResponseEntity<ApiResponse<Void>> deleteMenuItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.ok(ApiResponse.success("Menu item successfully deleted", null));
    }
}

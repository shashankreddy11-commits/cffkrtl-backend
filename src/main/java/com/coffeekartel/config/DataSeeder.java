package com.coffeekartel.config;

import com.coffeekartel.entity.Category;
import com.coffeekartel.entity.MenuItem;
import com.coffeekartel.entity.Role;
import com.coffeekartel.entity.User;
import com.coffeekartel.repository.CategoryRepository;
import com.coffeekartel.repository.MenuItemRepository;
import com.coffeekartel.repository.RoleRepository;
import com.coffeekartel.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Value("${app.admin.email:${ADMIN_EMAIL:}}")
    private String configuredAdminEmail;

    @org.springframework.beans.factory.annotation.Value("${app.admin.password:${ADMIN_PASSWORD:}}")
    private String configuredAdminPassword;

    public DataSeeder(RoleRepository roleRepository,
                      UserRepository userRepository,
                      CategoryRepository categoryRepository,
                      MenuItemRepository menuItemRepository,
                      PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.menuItemRepository = menuItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedRolesAndAdmin();
        seedCategoriesAndMenuItems();
    }

    private void seedRolesAndAdmin() {
        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_CUSTOMER")));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        if (configuredAdminEmail != null && !configuredAdminEmail.trim().isEmpty() &&
            configuredAdminPassword != null && !configuredAdminPassword.trim().isEmpty()) {

            String email = configuredAdminEmail.trim();
            if (!userRepository.existsByEmail(email)) {
                User admin = new User(
                        "Kartel Administrator",
                        email,
                        "9876543210",
                        passwordEncoder.encode(configuredAdminPassword.trim())
                );
                Set<Role> roles = new HashSet<>();
                roles.add(customerRole);
                roles.add(adminRole);
                admin.setRoles(roles);
                userRepository.save(admin);
                log.info("Initialized administrator account for: {}", email);
            }
        } else {
            log.info("No ADMIN_EMAIL or ADMIN_PASSWORD configured. Skipping automated admin creation.");
        }
    }

    private void seedCategoriesAndMenuItems() {
        Category coffeeCat = categoryRepository.findBySlug("coffee")
                .orElseGet(() -> categoryRepository.save(new Category("Craft Coffee", "coffee", 1)));

        Category beverageCat = categoryRepository.findBySlug("beverage")
                .orElseGet(() -> categoryRepository.save(new Category("Warm Drinks", "beverage", 2)));

        Category foodCat = categoryRepository.findBySlug("food")
                .orElseGet(() -> categoryRepository.save(new Category("Gourmet Bites", "food", 3)));

        seedItemIfMissing(
                coffeeCat,
                "caramel-coffee",
                "Caramel Coffee",
                "Signature Brews",
                "Smooth handcrafted espresso infused with rich buttery caramel drizzle and velvety steamed milk.",
                new BigDecimal("220.00"),
                true,
                true,
                "BEVERAGE"
        );

        seedItemIfMissing(
                coffeeCat,
                "french-vanilla-coffee",
                "French Vanilla Coffee",
                "Signature Brews",
                "A fragrant, comforting brew blended with natural Madagascar vanilla notes and silky textured micro-foam.",
                new BigDecimal("230.00"),
                true,
                true,
                "BEVERAGE"
        );

        seedItemIfMissing(
                beverageCat,
                "hot-chocolate",
                "Hot Chocolate",
                "Warm Indulgence",
                "Decadent Belgian cocoa melted into warm full-cream milk, whipped to creamy perfection.",
                new BigDecimal("190.00"),
                true,
                true,
                "BEVERAGE"
        );

        seedItemIfMissing(
                foodCat,
                "tempura-prawns",
                "Tempura Prawns",
                "Starters & Appetizers",
                "Crisp, golden Japanese-style battered prawns served with a delicate dipping sauce.",
                new BigDecimal("340.00"),
                false,
                true,
                "NON_VEG"
        );

        seedItemIfMissing(
                foodCat,
                "chicken-pizza",
                "Chicken Pizza",
                "Artisan Pizzas",
                "Freshly baked hand-stretched crust topped with seasoned chicken, rich herb tomato reduction, and melted mozzarella.",
                new BigDecimal("380.00"),
                false,
                true,
                "NON_VEG"
        );

        seedItemIfMissing(
                coffeeCat,
                "caramel-iced-latte",
                "Caramel Iced Latte",
                "Cold Brews & Iced",
                "Chilled espresso poured over ice and milk, sweetened with artisanal caramel.",
                new BigDecimal("240.00"),
                true,
                false,
                "BEVERAGE"
        );
    }

    private void seedItemIfMissing(Category category, String slug, String name,
                                   String subCategory, String description,
                                   BigDecimal price, boolean isSignature,
                                   boolean isPopular, String dietaryType) {
        if (menuItemRepository.findBySlug(slug).isEmpty()) {
            MenuItem item = new MenuItem(
                    category,
                    slug,
                    name,
                    subCategory,
                    description,
                    price,
                    isSignature,
                    isPopular,
                    dietaryType
            );
            menuItemRepository.save(item);
            log.info("Seeded catalog item: {}", name);
        }
    }
}

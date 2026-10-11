package com.coffeekartel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CoffeeKartelApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Application Context Loads Successfully")
    void contextLoads() {
    }

    @Test
    @DisplayName("Health endpoint returns UP without authentication")
    void healthEndpointReturnsUp() throws Exception {
        mockMvc.perform(get("/api/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("The Coffee Kartel Backend"));
    }

    @Test
    @DisplayName("OpenAPI docs endpoint is accessible publicly")
    void openApiDocsAccessible() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("The Coffee Kartel API"));
    }

    @Test
    @DisplayName("Public menu endpoint returns seeded items")
    void publicMenuEndpointReturnsItems() throws Exception {
        mockMvc.perform(get("/api/menu")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))))
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    @DisplayName("Public categories endpoint returns seeded categories")
    void publicCategoriesEndpointReturnsCategories() throws Exception {
        mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$[0].slug").exists());
    }

    @Test
    @DisplayName("Submitting table reservation succeeds and generates confirmation code")
    void submitReservationSucceeds() throws Exception {
        String reservationJson = """
                {
                    "name": "Arjun Rao",
                    "phone": "9876543210",
                    "date": "2026-11-20",
                    "time": "19:30:00",
                    "partySize": 4,
                    "specialRequests": "Corner table with quiet atmosphere"
                }
                """;

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservationJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.confirmationCode", startsWith("CK-RES-")))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.name").value("Arjun Rao"));
    }

    @Test
    @DisplayName("Submitting table reservation with invalid phone fails with 400 Bad Request")
    void submitReservationInvalidFails() throws Exception {
        String invalidJson = """
                {
                    "name": "",
                    "phone": "",
                    "date": "2026-11-20",
                    "time": "19:30:00",
                    "partySize": 25
                }
                """;

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    @DisplayName("Submitting contact feedback message succeeds")
    void submitContactMessageSucceeds() throws Exception {
        String contactJson = """
                {
                    "name": "Sunita Reddy",
                    "contactInfo": "sunita@example.com",
                    "message": "Loved the French Vanilla brew! Inquiring about weekend event hosting."
                }
                """;

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Sunita Reddy"));
    }

    @Test
    @DisplayName("Customer registration and login returns JWT token")
    void customerRegistrationAndLoginFlow() throws Exception {
        String registerJson = """
                {
                    "fullName": "Priya Sharma",
                    "email": "priya.sharma@example.com",
                    "phone": "9123456789",
                    "password": "Password@123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("priya.sharma@example.com"));

        String loginJson = """
                {
                    "username": "priya.sharma@example.com",
                    "password": "Password@123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.fullName").value("Priya Sharma"));
    }

    @Test
    @DisplayName("Guest customer places an order successfully")
    void guestPlacesOrderSuccessfully() throws Exception {
        String orderJson = """
                {
                    "customerName": "Ramesh Kumar",
                    "customerPhone": "9988776655",
                    "serviceType": "DINE_IN",
                    "tableNumber": "Table 4",
                    "specialNotes": "Extra napkins please",
                    "items": [
                        {
                            "menuItemId": 1,
                            "quantity": 2
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber", startsWith("CK-ORD-")))
                .andExpect(jsonPath("$.orderStatus").value("RECEIVED"))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    @DisplayName("Protected customer endpoint rejects unauthenticated requests with 401 Unauthorized")
    void protectedEndpointRejectsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/orders/my-orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("Protected admin endpoint rejects unauthenticated requests with 401 Unauthorized")
    void adminEndpointRejectsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/admin/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}

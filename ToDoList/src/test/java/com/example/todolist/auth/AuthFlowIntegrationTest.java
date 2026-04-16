package com.example.todolist.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
class AuthFlowIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void registerThenAccessProtectedEndpointWithBearerToken() throws Exception {
        String token = registerAndExtractToken("tester@example.com");

        mockMvc.perform(get("/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tester"))
                .andExpect(jsonPath("$.email").value("tester@example.com"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void createAndReadTodosForAuthenticatedUser() throws Exception {
        String token = registerAndExtractToken("todo-user@example.com");

        mockMvc.perform(post("/todos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Write tests",
                                  "description": "Cover auth and todo flows"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Write tests"))
                .andExpect(jsonPath("$.description").value("Cover auth and todo flows"))
                .andExpect(jsonPath("$.id").isNumber());

        mockMvc.perform(get("/todos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Write tests"))
                .andExpect(jsonPath("$.content[0].description").value("Cover auth and todo flows"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    private String registerAndExtractToken(String email) throws Exception {
        MvcResult registerResult = mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Tester",
                                  "email": "%s",
                                  "password": "password123"
                                }
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        String responseBody = registerResult.getResponse().getContentAsString();
        return extractToken(responseBody);
    }

    private String extractToken(String responseBody) {
        String prefix = "{\"token\":\"";
        return responseBody.substring(prefix.length(), responseBody.length() - 2);
    }
}

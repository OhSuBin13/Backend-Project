package com.example.url_shortening.controller;

import com.example.url_shortening.dto.LinkDto;
import com.example.url_shortening.exception.LinkNotFoundException;
import com.example.url_shortening.service.LinkService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LinkController.class)
class LinkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LinkService linkService;

    @Test
    void createReturnsBadRequestWhenBodyIsMissing() throws Exception {
        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateReturnsBadRequestWhenUrlIsBlank() throws Exception {
        mockMvc.perform(put("/shorten/abc12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "url": "   "
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingShortCodeReturnsNotFound() throws Exception {
        Mockito.when(linkService.getByShortCode("missing"))
                .thenThrow(new LinkNotFoundException("missing"));

        mockMvc.perform(get("/shorten/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Link not found"))
                .andExpect(jsonPath("$.detail").value("Link not found for shortCode: missing"));
    }

    @Test
    void createReturnsCreatedWhenUrlIsPresent() throws Exception {
        LinkDto response = LinkDto.builder()
                .id(1L)
                .url("https://example.com")
                .shortCode("abc12")
                .build();

        Mockito.when(linkService.create(Mockito.any(LinkDto.class))).thenReturn(response);

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "url": "https://example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("abc12"));
    }
}

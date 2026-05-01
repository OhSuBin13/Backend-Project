package com.osb.shopapp.address;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osb.shopapp.TestDataUtils;
import com.osb.shopapp.common.AppConstant;
import com.osb.shopapp.common.PageResponse;
import com.osb.shopapp.configuration.TestConfig;
import com.osb.shopapp.token.JwtFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AddressController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
)
@Import(TestConfig.class)
@WithMockUser(roles = "ADMIN")
public class AddressControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AddressService addressService;

    private AddressRequest addressRequestA;
    private AddressResponse addressResponseA;
    private AddressResponse addressResponseB;

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        addressRequestA = TestDataUtils.createAddressRequestA();
        addressResponseA = TestDataUtils.createAddressResponseA(1);
        addressResponseB = TestDataUtils.createAddressResponseB(2);
    }

    @Test
    public void shouldSaveAddressWhenValidRequest() throws Exception {
        when(addressService.save(any(AddressRequest.class), any(Authentication.class))).thenReturn(addressResponseA);

        MvcResult mvcResult = mockMvc.perform(post("/api/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequestA))
        ).andExpect(status().isCreated()).andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();

        assertThat(jsonResponse).isEqualTo(objectMapper.writeValueAsString(addressResponseA));
    }

    @Test
    public void shouldNotSaveAddressWhenInvalidRequest() throws Exception {
        addressRequestA.setCountry(null);
        addressRequestA.setCity(null);
        Map<String, String> expectedErrors = new HashMap<>();
        expectedErrors.put("country", "Country must not be empty");
        expectedErrors.put("city", "City must not be empty");

        MvcResult mvcResult = mockMvc.perform(post("/api/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequestA))
        ).andExpect(status().isBadRequest()).andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();

        assertThat(jsonResponse).isEqualTo(objectMapper.writeValueAsString(expectedErrors));
    }

    @Test
    public void shouldFindAllAddresses() throws Exception {
        PageResponse<AddressResponse> pageResponse = new PageResponse<>(
                List.of(addressResponseA, addressResponseB),
                AppConstant.PAGE_NUMBER_INT, AppConstant.PAGE_SIZE_INT,
                2, 1,
                true, true
        );

        when(addressService.findAll(
                AppConstant.PAGE_NUMBER_INT, AppConstant.PAGE_SIZE_INT,
                AppConstant.SORT_ADDRESSES_BY, AppConstant.SORT_DIR
        )).thenReturn(pageResponse);

        MvcResult mvcResult = mockMvc.perform(get("/api/addresses")
        ).andExpect(status().isOk()).andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();

        assertThat(jsonResponse).isEqualTo(objectMapper.writeValueAsString(pageResponse));
    }

    @Test
    public void shouldFindAllAddressByUserId() throws Exception {
        List<AddressResponse> addressResponses = List.of(addressResponseA);

        when(addressService.findAllByUserId(eq(addressResponseA.getUserId()), any(Authentication.class)))
                .thenReturn(addressResponses);

        MvcResult mvcResult = mockMvc.perform(get("/api/users/" + addressResponseA.getUserId() + "/addresses")
        ).andExpect(status().isOk()).andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();

        assertThat(jsonResponse).isEqualTo(objectMapper.writeValueAsString(addressResponses));
    }

    @Test
    public void shouldMakeAddressMain() throws Exception {
        addressResponseA.setIsMain(true);

        when(addressService.makeMain(eq(addressResponseA.getId()), any(Authentication.class)))
                .thenReturn(addressResponseA);

        MvcResult mvcResult = mockMvc.perform(patch("/api/addresses/" + addressResponseA.getId() + "/main")
        ).andExpect(status().isOk()).andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();

        assertThat(jsonResponse).isEqualTo(objectMapper.writeValueAsString(addressResponseA));
    }

    @Test
    public void shouldUpdateAddressWhenValidRequest() throws Exception {
        addressRequestA.setCountry("Updated country");
        addressRequestA.setCity("Updated city");
        addressResponseA.setCountry("Updated country");
        addressResponseA.setCity("Updated city");

        when(addressService.update(any(AddressRequest.class), eq(addressResponseA.getId()), any(Authentication.class)))
                .thenReturn(addressResponseA);

        MvcResult mvcResult = mockMvc.perform(put("/api/addresses/" + addressResponseA.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequestA))
        ).andExpect(status().isOk()).andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();

        assertThat(jsonResponse).isEqualTo(objectMapper.writeValueAsString(addressResponseA));
    }

    @Test
    public void shouldNotUpdateAddressWhenInvalidRequest() throws Exception {
        addressRequestA.setCountry(null);
        addressRequestA.setCity(null);

        Map<String, String> expectedErrors = new HashMap<>();
        expectedErrors.put("country", "Country must not be empty");
        expectedErrors.put("city", "City must not be empty");

        MvcResult mvcResult = mockMvc.perform(put("/api/addresses/" + addressResponseA.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequestA))
        ).andExpect(status().isBadRequest()).andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();

        assertThat(jsonResponse).isEqualTo(objectMapper.writeValueAsString(expectedErrors));
    }

    @Test
    public void shouldDeleteAddress() throws Exception {
        mockMvc.perform(delete("/api/addresses/" + addressResponseA.getId()))
                .andExpect(status().isNoContent());

        verify(addressService, times(1)).delete(eq(addressResponseA.getId()), any(Authentication.class));
    }
}

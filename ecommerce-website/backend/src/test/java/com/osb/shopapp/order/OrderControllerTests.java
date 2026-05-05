package com.osb.shopapp.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osb.shopapp.TestDataUtils;
import com.osb.shopapp.address.AddressResponse;
import com.osb.shopapp.category.CategoryResponse;
import com.osb.shopapp.common.AppConstants;
import com.osb.shopapp.common.PageResponse;
import com.osb.shopapp.configuration.TestConfig;
import com.osb.shopapp.product.ProductResponse;
import com.osb.shopapp.role.Role;
import com.osb.shopapp.token.JwtFilter;
import com.osb.shopapp.user.UserResponse;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = OrderController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
)
@Import(TestConfig.class)
@WithMockUser(roles = "ADMIN")
public class OrderControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private OrderRequest orderRequestA;
    private OrderResponse orderResponseA;
    private OrderResponse orderResponseB;

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        UserResponse adminUserResponse = TestDataUtils.createUserResponseA(
                Set.of(new Role(1, "ADMIN"))
        );
        UserResponse userResponse = TestDataUtils.createUserResponseB(
                Set.of(new Role(2, "USER"))
        );

        AddressResponse addressResponseA = TestDataUtils.createAddressResponseA(adminUserResponse.getId());
        AddressResponse addressResponseB = TestDataUtils.createAddressResponseB(userResponse.getId());

        CategoryResponse categoryResponse = TestDataUtils.createCategoryResponseA();
        ProductResponse productResponseA = TestDataUtils.createProductResponseA(categoryResponse, adminUserResponse);
        ProductResponse productResponseB = TestDataUtils.createProductResponseB(categoryResponse, userResponse);

        orderRequestA = TestDataUtils.createOrderRequestA(1);
        orderResponseA = TestDataUtils.createOrderResponseA(
                addressResponseA, adminUserResponse, productResponseB
        );
        orderResponseB = TestDataUtils.createOrderResponseB(
                addressResponseB, userResponse, productResponseA
        );
    }

    @Test
    public void shouldSaveOrderWhenValidRequest() throws Exception {
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("redirectUrl", "url");

        when(orderService.save(any(OrderRequest.class), any(Authentication.class))).thenReturn(expectedResponse);

        MvcResult mvcResult = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequestA))
        ).andExpect(status().isCreated()).andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();

        assertThat(jsonResponse).isEqualTo(objectMapper.writeValueAsString(expectedResponse));
    }

    @Test
    public void shouldNotSaveOrderWhenInvalidRequest() throws Exception {
        orderRequestA.setBillingAddressId(null);
        Map<String, String> expectedErrors = new HashMap<>();
        expectedErrors.put("billingAddressId", "Billing address ID must not be empty");

        MvcResult mvcResult = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequestA))
        ).andExpect(status().isBadRequest()).andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();

        assertThat(jsonResponse).isEqualTo(objectMapper.writeValueAsString(expectedErrors));
    }

    @Test
    public void shouldHandleStripeEvent() throws Exception {
        String payload = "payload";
        String sigHeader = "sigHeader";

        when(orderService.handleStripeEvent(payload, sigHeader)).thenReturn("success");

        MvcResult mvcResult = mockMvc.perform(post("/api/orders/stripe-webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .header("Stripe-Signature", sigHeader)
        ).andExpect(status().isOk()).andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();

        assertThat(jsonResponse).isEqualTo("success");
    }

    @Test
    public void shouldFulfillOrder() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("stripeSessionId", "sessionId");

        mockMvc.perform(post("/api/orders/fulfill")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        ).andExpect(status().isNoContent());

        verify(orderService, times(1)).fulfillOrder("sessionId");
    }

    @Test
    public void shouldFindAllOrders() throws Exception {
        PageResponse<OrderResponse> pageResponse = new PageResponse<>(
                List.of(orderResponseA, orderResponseB),
                AppConstants.PAGE_NUMBER_INT, AppConstants.PAGE_SIZE_INT,
                2, 1,
                true, true
        );

        when(orderService.findAll(
                AppConstants.PAGE_NUMBER_INT, AppConstants.PAGE_SIZE_INT,
                AppConstants.SORT_ORDERS_BY, AppConstants.SORT_DIR
        )).thenReturn(pageResponse);

        MvcResult mvcResult = mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();

        assertThat(jsonResponse).isEqualTo(objectMapper.writeValueAsString(pageResponse));
    }
}

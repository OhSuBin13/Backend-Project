package com.osb.shopapp.product;

import com.osb.shopapp.category.CategoryRepository;
import com.osb.shopapp.file.FileService;
import com.osb.shopapp.user.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {

    public static final MockMultipartFile TEST_IMAGE_1;
    public static final MockMultipartFile TEST_IMAGE_2;

    static {
        try {
            TEST_IMAGE_1 = new MockMultipartFile(
                    "image1", "test_image_1.jpeg", "image/jpeg",
                    Files.readAllBytes(Path.of("src/test/resources/test_image_1.jpeg"))
            );
            TEST_IMAGE_2 = new MockMultipartFile(
                    "image2", "test_image_2.jpeg", "image/jpeg",
                    Files.readAllBytes(Path.of("src/test/resources/test_image_2.jpeg"))
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private FileService fileService;

    @InjectMocks
    private ProductService productService;

    private Product productA;
    private Product productB;
    private ProductRequest productRequestA;
    private ProductRequest productRequest;
}

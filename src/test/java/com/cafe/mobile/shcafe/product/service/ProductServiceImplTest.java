package com.cafe.mobile.shcafe.product.service;

import com.cafe.mobile.shcafe.product.dto.response.AllProductResponse;
import com.cafe.mobile.shcafe.product.dto.response.CategoryResponse;
import com.cafe.mobile.shcafe.product.dto.response.ProductResponse;
import com.cafe.mobile.shcafe.product.entity.Category;
import com.cafe.mobile.shcafe.product.entity.Product;
import com.cafe.mobile.shcafe.product.entity.ProductHistory;
import com.cafe.mobile.shcafe.product.repository.CategoryRepository;
import com.cafe.mobile.shcafe.product.repository.ProductHistoryRepository;
import com.cafe.mobile.shcafe.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 테스트")
class ProductServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductHistoryRepository productHistoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Nested
    @DisplayName("전체 상품 조회 테스트")
    class GetAllProductTest {

        @Test
        @DisplayName("정상 케이스 - 카테고리와 상품이 모두 있는 경우")
        void success_withCategoriesAndProducts() {
            // Given
            Category category1 = Category.builder()
                    .categoryId(1L)
                    .categoryName("커피")
                    .displayOrder(1)
                    .useYn("Y")
                    .regDtm(LocalDateTime.now())
                    .build();

            Category category2 = Category.builder()
                    .categoryId(2L)
                    .categoryName("디저트")
                    .displayOrder(2)
                    .useYn("Y")
                    .regDtm(LocalDateTime.now())
                    .build();

            List<Category> categories = List.of(category1, category2);

            Product product1 = Product.builder()
                    .productId(1L)
                    .category(category1)
                    .productName("아메리카노")
                    .price(4000)
                    .useYn("Y")
                    .regDtm(LocalDateTime.now())
                    .build();

            Product product2 = Product.builder()
                    .productId(2L)
                    .category(category1)
                    .productName("라떼")
                    .price(4500)
                    .useYn("Y")
                    .regDtm(LocalDateTime.now())
                    .build();

            Product product3 = Product.builder()
                    .productId(3L)
                    .category(category2)
                    .productName("케이크")
                    .price(6000)
                    .useYn("Y")
                    .regDtm(LocalDateTime.now())
                    .build();

            when(categoryRepository.findByUseYnOrderByDisplayOrderAsc("Y"))
                    .thenReturn(categories);
            when(productRepository.findUseYnProducts(1L, "Y"))
                    .thenReturn(List.of(product1, product2));
            when(productRepository.findUseYnProducts(2L, "Y"))
                    .thenReturn(List.of(product3));

            // When
            List<AllProductResponse> result = productService.getAllProduct();

            // Then
            assertThat(result).hasSize(2);
            
            // 첫 번째 카테고리 검증
            AllProductResponse firstCategory = result.get(0);
            assertThat(firstCategory.getCategoryId()).isEqualTo(1L);
            assertThat(firstCategory.getCategoryName()).isEqualTo("커피");
            assertThat(firstCategory.getProducts()).hasSize(2);
            assertThat(firstCategory.getProducts().get(0).getProductName()).isEqualTo("아메리카노");
            assertThat(firstCategory.getProducts().get(1).getProductName()).isEqualTo("라떼");

            // 두 번째 카테고리 검증
            AllProductResponse secondCategory = result.get(1);
            assertThat(secondCategory.getCategoryId()).isEqualTo(2L);
            assertThat(secondCategory.getCategoryName()).isEqualTo("디저트");
            assertThat(secondCategory.getProducts()).hasSize(1);
            assertThat(secondCategory.getProducts().get(0).getProductName()).isEqualTo("케이크");

            verify(categoryRepository).findByUseYnOrderByDisplayOrderAsc("Y");
            verify(productRepository).findUseYnProducts(1L, "Y");
            verify(productRepository).findUseYnProducts(2L, "Y");
        }

        @Test
        @DisplayName("카테고리는 있지만 상품이 없는 경우")
        void success_withCategoriesButNoProducts() {
            // Given
            Category category = Category.builder()
                    .categoryId(1L)
                    .categoryName("커피")
                    .displayOrder(1)
                    .useYn("Y")
                    .regDtm(LocalDateTime.now())
                    .build();

            when(categoryRepository.findByUseYnOrderByDisplayOrderAsc("Y"))
                    .thenReturn(List.of(category));
            when(productRepository.findUseYnProducts(1L, "Y"))
                    .thenReturn(new ArrayList<>());

            // When
            List<AllProductResponse> result = productService.getAllProduct();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCategoryId()).isEqualTo(1L);
            assertThat(result.get(0).getCategoryName()).isEqualTo("커피");
            assertThat(result.get(0).getProducts()).isEmpty();

            verify(categoryRepository).findByUseYnOrderByDisplayOrderAsc("Y");
            verify(productRepository).findUseYnProducts(1L, "Y");
        }

        @Test
        @DisplayName("카테고리가 없는 경우")
        void success_withNoCategories() {
            // Given
            when(categoryRepository.findByUseYnOrderByDisplayOrderAsc("Y"))
                    .thenReturn(new ArrayList<>());

            // When
            List<AllProductResponse> result = productService.getAllProduct();

            // Then
            assertThat(result).isEmpty();

            verify(categoryRepository).findByUseYnOrderByDisplayOrderAsc("Y");
            verify(productRepository, never()).findUseYnProducts(any(), any());
        }
    }

    @Nested
    @DisplayName("전체 카테고리 조회 테스트")
    class GetAllCategoriesTest {

        @Test
        @DisplayName("정상 케이스")
        void success() {
            // Given
            Category category1 = Category.builder()
                    .categoryId(1L)
                    .categoryName("커피")
                    .displayOrder(1)
                    .useYn("Y")
                    .regDtm(LocalDateTime.now())
                    .build();

            Category category2 = Category.builder()
                    .categoryId(2L)
                    .categoryName("디저트")
                    .displayOrder(2)
                    .useYn("Y")
                    .regDtm(LocalDateTime.now())
                    .build();

            List<Category> categories = List.of(category1, category2);

            when(categoryRepository.findByUseYnOrderByDisplayOrderAsc("Y"))
                    .thenReturn(categories);

            // When
            List<CategoryResponse> result = productService.getAllCategories();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getCategoryId()).isEqualTo(1L);
            assertThat(result.get(0).getCategoryName()).isEqualTo("커피");
            assertThat(result.get(1).getCategoryId()).isEqualTo(2L);
            assertThat(result.get(1).getCategoryName()).isEqualTo("디저트");

            verify(categoryRepository).findByUseYnOrderByDisplayOrderAsc("Y");
        }

        @Test
        @DisplayName("카테고리가 없는 경우")
        void success_withNoCategories() {
            // Given
            when(categoryRepository.findByUseYnOrderByDisplayOrderAsc("Y"))
                    .thenReturn(new ArrayList<>());

            // When
            List<CategoryResponse> result = productService.getAllCategories();

            // Then
            assertThat(result).isEmpty();

            verify(categoryRepository).findByUseYnOrderByDisplayOrderAsc("Y");
        }
    }

    @Nested
    @DisplayName("카테고리별 상품 조회 테스트")
    class GetProductsByCategoryTest {

        @Test
        @DisplayName("정상 케이스")
        void success() {
            // Given
            Long categoryId = 1L;
            Category category = Category.builder()
                    .categoryId(categoryId)
                    .categoryName("커피")
                    .build();

            Product product1 = Product.builder()
                    .productId(1L)
                    .category(category)
                    .productName("아메리카노")
                    .price(4000)
                    .useYn("Y")
                    .regDtm(LocalDateTime.now())
                    .build();

            Product product2 = Product.builder()
                    .productId(2L)
                    .category(category)
                    .productName("라떼")
                    .price(4500)
                    .useYn("Y")
                    .regDtm(LocalDateTime.now())
                    .build();

            List<Product> products = List.of(product1, product2);

            when(productRepository.findUseYnProducts(categoryId, "Y"))
                    .thenReturn(products);

            // When
            List<ProductResponse> result = productService.getProductsByCategory(categoryId);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getProductName()).isEqualTo("아메리카노");
            assertThat(result.get(0).getPrice()).isEqualTo(4000);
            assertThat(result.get(1).getProductName()).isEqualTo("라떼");
            assertThat(result.get(1).getPrice()).isEqualTo(4500);

            verify(productRepository).findUseYnProducts(categoryId, "Y");
        }

        @Test
        @DisplayName("해당 카테고리에 상품이 없는 경우")
        void success_withNoProducts() {
            // Given
            Long categoryId = 1L;

            when(productRepository.findUseYnProducts(categoryId, "Y"))
                    .thenReturn(new ArrayList<>());

            // When
            List<ProductResponse> result = productService.getProductsByCategory(categoryId);

            // Then
            assertThat(result).isEmpty();

            verify(productRepository).findUseYnProducts(categoryId, "Y");
        }

        @Test
        @DisplayName("null 카테고리 ID")
        void success_withNullCategoryId() {
            // Given
            Long categoryId = null;

            when(productRepository.findUseYnProducts(categoryId, "Y"))
                    .thenReturn(new ArrayList<>());

            // When
            List<ProductResponse> result = productService.getProductsByCategory(categoryId);

            // Then
            assertThat(result).isEmpty();

            verify(productRepository).findUseYnProducts(categoryId, "Y");
        }
    }

    @Nested
    @DisplayName("상품 ID로 조회 테스트")
    class FindByProductIdAndUseYnTest {

        @Test
        @DisplayName("정상 케이스 - 상품이 존재하는 경우")
        void success_productExists() {
            // Given
            Long productId = 1L;
            String useYn = "Y";
            
            Category category = Category.builder()
                    .categoryId(1L)
                    .categoryName("커피")
                    .build();

            Product product = Product.builder()
                    .productId(productId)
                    .category(category)
                    .productName("아메리카노")
                    .price(4000)
                    .useYn(useYn)
                    .regDtm(LocalDateTime.now())
                    .build();

            when(productRepository.findByProductIdAndUseYn(productId, useYn))
                    .thenReturn(Optional.of(product));

            // When
            Optional<Product> result = productService.findByProductIdAndUseYn(productId, useYn);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getProductId()).isEqualTo(productId);
            assertThat(result.get().getProductName()).isEqualTo("아메리카노");
            assertThat(result.get().getUseYn()).isEqualTo(useYn);

            verify(productRepository).findByProductIdAndUseYn(productId, useYn);
        }

        @Test
        @DisplayName("상품이 존재하지 않는 경우")
        void success_productNotExists() {
            // Given
            Long productId = 999L;
            String useYn = "Y";

            when(productRepository.findByProductIdAndUseYn(productId, useYn))
                    .thenReturn(Optional.empty());

            // When
            Optional<Product> result = productService.findByProductIdAndUseYn(productId, useYn);

            // Then
            assertThat(result).isEmpty();

            verify(productRepository).findByProductIdAndUseYn(productId, useYn);
        }

        @Test
        @DisplayName("사용하지 않는 상품 조회")
        void success_inactiveProduct() {
            // Given
            Long productId = 1L;
            String useYn = "N";

            when(productRepository.findByProductIdAndUseYn(productId, useYn))
                    .thenReturn(Optional.empty());

            // When
            Optional<Product> result = productService.findByProductIdAndUseYn(productId, useYn);

            // Then
            assertThat(result).isEmpty();

            verify(productRepository).findByProductIdAndUseYn(productId, useYn);
        }

        @Test
        @DisplayName("null 상품 ID")
        void success_nullProductId() {
            // Given
            Long productId = null;
            String useYn = "Y";

            when(productRepository.findByProductIdAndUseYn(productId, useYn))
                    .thenReturn(Optional.empty());

            // When
            Optional<Product> result = productService.findByProductIdAndUseYn(productId, useYn);

            // Then
            assertThat(result).isEmpty();

            verify(productRepository).findByProductIdAndUseYn(productId, useYn);
        }
    }

    @Nested
    @DisplayName("특정 시점 상품 이력 조회 테스트")
    class FindProductInfoAtTimeUseYnTest {

        @Test
        @DisplayName("정상 케이스 - 상품 이력이 존재하는 경우")
        void success_productHistoryExists() {
            // Given
            Long productId = 1L;
            LocalDateTime time = LocalDateTime.of(2024, 1, 15, 10, 0);
            String useYn = "Y";

            Category category = Category.builder()
                    .categoryId(1L)
                    .categoryName("커피")
                    .build();

            Product product = Product.builder()
                    .productId(productId)
                    .category(category)
                    .productName("아메리카노")
                    .price(4000)
                    .useYn(useYn)
                    .regDtm(LocalDateTime.now())
                    .build();

            ProductHistory productHistory = ProductHistory.builder()
                    .historyId(1L)
                    .productId(productId)
                    .categoryId(1L)
                    .productName("아메리카노")
                    .price(4000)
                    .useYn(useYn)
                    .regDtm(time)
                    .build();

            when(productHistoryRepository.findProductInfoAtTimeUseYn(productId, time, useYn))
                    .thenReturn(Optional.of(productHistory));

            // When
            Optional<ProductHistory> result = productService.findProductInfoAtTimeUseYn(productId, time, useYn);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getProductId()).isEqualTo(productId);
            assertThat(result.get().getPrice()).isEqualTo(4000);
            assertThat(result.get().getUseYn()).isEqualTo(useYn);

            verify(productHistoryRepository).findProductInfoAtTimeUseYn(productId, time, useYn);
        }

        @Test
        @DisplayName("상품 이력이 존재하지 않는 경우")
        void success_productHistoryNotExists() {
            // Given
            Long productId = 1L;
            LocalDateTime time = LocalDateTime.of(2024, 1, 15, 10, 0);
            String useYn = "Y";

            when(productHistoryRepository.findProductInfoAtTimeUseYn(productId, time, useYn))
                    .thenReturn(Optional.empty());

            // When
            Optional<ProductHistory> result = productService.findProductInfoAtTimeUseYn(productId, time, useYn);

            // Then
            assertThat(result).isEmpty();

            verify(productHistoryRepository).findProductInfoAtTimeUseYn(productId, time, useYn);
        }

        @Test
        @DisplayName("사용하지 않는 상품 이력 조회")
        void success_inactiveProductHistory() {
            // Given
            Long productId = 1L;
            LocalDateTime time = LocalDateTime.of(2024, 1, 15, 10, 0);
            String useYn = "N";

            when(productHistoryRepository.findProductInfoAtTimeUseYn(productId, time, useYn))
                    .thenReturn(Optional.empty());

            // When
            Optional<ProductHistory> result = productService.findProductInfoAtTimeUseYn(productId, time, useYn);

            // Then
            assertThat(result).isEmpty();

            verify(productHistoryRepository).findProductInfoAtTimeUseYn(productId, time, useYn);
        }

        @Test
        @DisplayName("null 상품 ID")
        void success_nullProductId() {
            // Given
            Long productId = null;
            LocalDateTime time = LocalDateTime.of(2024, 1, 15, 10, 0);
            String useYn = "Y";

            when(productHistoryRepository.findProductInfoAtTimeUseYn(productId, time, useYn))
                    .thenReturn(Optional.empty());

            // When
            Optional<ProductHistory> result = productService.findProductInfoAtTimeUseYn(productId, time, useYn);

            // Then
            assertThat(result).isEmpty();

            verify(productHistoryRepository).findProductInfoAtTimeUseYn(productId, time, useYn);
        }

        @Test
        @DisplayName("null 시간")
        void success_nullTime() {
            // Given
            Long productId = 1L;
            LocalDateTime time = null;
            String useYn = "Y";

            when(productHistoryRepository.findProductInfoAtTimeUseYn(productId, time, useYn))
                    .thenReturn(Optional.empty());

            // When
            Optional<ProductHistory> result = productService.findProductInfoAtTimeUseYn(productId, time, useYn);

            // Then
            assertThat(result).isEmpty();

            verify(productHistoryRepository).findProductInfoAtTimeUseYn(productId, time, useYn);
        }
    }
}

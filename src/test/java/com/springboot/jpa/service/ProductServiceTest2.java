package com.springboot.jpa.service;
//Mock 객체를 활용하지않고 @MockBean으로 스프링컨테이너에 Mock 객체를 주입받는 방법

import com.springboot.jpa.data.dto.ProductDto;
import com.springboot.jpa.data.dto.ProductResponseDto;
import com.springboot.jpa.data.entity.Product;
import com.springboot.jpa.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@Import(ProductServiceImpl.class)
public class ProductServiceTest2 {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ProductService productService(ProductRepository productRepository) {
            return new ProductServiceImpl(productRepository);
        }
    }


    @MockitoBean
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    @BeforeEach
    public void setupTest() {
        productService = new ProductServiceImpl(productRepository);
    }

    /**
     * throws Exception 붙인 이유:
     * 코드 안에서 getDeclaredField()와 Field.set() 같은 리플렉션 메서드가 checked exception을 던지기 때문
     */
    @Test
    @DisplayName("Product 조회 테스트 ")
    void getProductTest() throws Exception {

        //Product 객체 생성
        Product givenProduct = Product.create("펜", 1000, 1234);
        Field numberField = Product.class.getDeclaredField("number");
        numberField.setAccessible(true);
        numberField.set(givenProduct, 123L);

        //123L로 조회할 때, givenProduct를 반환하도록 동작 정의
        Mockito.when(productRepository.selectProduct(123L))
                .thenReturn(givenProduct);


        ProductResponseDto productResponseDto = productService.getProduct(123L);

        //검증
        Assertions.assertEquals(productResponseDto.getNumber(), givenProduct.getNumber());
        Assertions.assertEquals(productResponseDto.getName(), givenProduct.getName());
        Assertions.assertEquals(productResponseDto.getPrice(), givenProduct.getPrice());
        Assertions.assertEquals(productResponseDto.getStock(), givenProduct.getStock());

        //검증 보완
        verify(productRepository).selectProduct(123L);

    }


    @Test
    @DisplayName("Product 생성 테스트")
    void saveProductTest() throws Exception {
        /**
         * any() : Mock 객체의 동작을 정의하거나 검증하는 단계에서 조건으로 특정 매개변수의 전달을 설정하지 않고
         * 메서드의 실행만을 확인하거나 좀더 큰 범위의 클래스 객체를 매개변수로 전달받는 등의 상황에서 사용
         *
         * then(returnsFirstArg()) : 메서드 호출 시 전달된 첫번째 인자를 그대로 반환
         */
        //동작 정의 (어떤 Product객체가 들어오든 insertProduct 실행시 그 Product를 반환하라
        Mockito.when(productRepository.insertProduct(any(Product.class)))
                .then(returnsFirstArg());


        ProductResponseDto productResponseDto = productService.saveProduct(
                new ProductDto("펜", 1000, 1234));

        Assertions.assertEquals(productResponseDto.getName(), "펜");
        Assertions.assertEquals(productResponseDto.getPrice(), 1000);
        Assertions.assertEquals(productResponseDto.getStock(), 1234);

        verify(productRepository).insertProduct(any());
    }

}

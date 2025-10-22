package com.springboot.jpa.controller;

import com.google.gson.Gson;
import com.springboot.jpa.data.dto.ProductDto;
import com.springboot.jpa.data.dto.ProductResponseDto;
import com.springboot.jpa.data.entity.Product;
import com.springboot.jpa.service.ProductServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @WebMvcTest - 슬라이스 테스트
 *   - 단위테스트와 통합 테스트의 중간 개념
 *   - 레이어드 아키텍처 기분 각 레이어별로 나누어 테스트를 진행한다는 의미
 */
@WebMvcTest(ProductController.class) //웹에서 사용되는 요청과 응답에 대한 테스트 수행 - 대상 클래스만 로드해 테스트 수행 (@SpringBootTest보다 가벼움)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; //컨트롤러 API를 테스트하기 위해 사용된 객체. (서블릿 컨테이너의 구동 없이 가상의 MVC 환경에서 모의 HTTP 서블릿을 요청하는 유틸리티 클래스)

    @MockitoBean //실제 빈 객체가 아닌 가짜(Mock) 객체 생성 주입 -> given()을 통해 동작 정의해야 함
    ProductServiceImpl productService;

    @Test
    @DisplayName("MockMvc를 통한 Product 데이터 가져오기 테스트")
    void getProductTest() throws Exception {
        //given

        //Entity에 setter메서드를 넣어주지 않았으므로 미리 생성후 willReturn에 대입
        Product product = Product.create("pen", 5000, 2000, LocalDateTime.now(), LocalDateTime.now());
        //number 값 직접 넣어주기
        Field numberField = Product.class.getDeclaredField("number");
        numberField.setAccessible(true);
        numberField.set(product, 123L);

        /**
         * given() : 이 객체어 어떤 메서드가 호출되고 어떤 파라미터를 받는지 가정
         * willReturn() : 어떤 결과를 리턴할 것인지 정의
         */
        given(productService.getProduct(123L)).willReturn(
               ProductResponseDto.from(product)
        );

        String productId = "123";


        //when-then
        /**
         * perform() : 서버로 URL 요청을 보내는 것처럼 통신 테스트 코드를 작성해서 컨트롤러를 테스트할 수 있음
         *      - return : ResultActions 객체
         *          -> andExpect() 메서드로 결괏값을 검증할 수 있음 (MockMvcResultMatchers의 메서드들 활용)
         *
         * MockMvcRequestBuilders
         *      - GET, POST, PUT, DELETE이 매핑되는 메서드로 URL을 정의해서 사용
         *      - return: MockHttpServletRequestBuilder (HTTP 요청 정보를 설정할 수 있음)
         * andDo() : 요청과 응답의 전체 내용 확인
         */
        mockMvc.perform(
                get("/product?number=" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.number").exists()
                )
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.price").exists())
                .andExpect(jsonPath("$.stock").exists())
                .andDo(print()
        );

        //verify() : 지정된 메서드가 실행되었는지 검증 -> 일반적으로 given()에 정의된 동작과 대응
        verify(productService).getProduct(123L);
    }

    @Test
    @DisplayName("Product 데이터 생성 테스트")
    void createProductTest() throws Exception {

        //Mock 객체에서 특정 메서드가 실행되는 경우 실제 Return을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        //productDto와 ProductResponseDtor가 객체가 달라서 값이 같아도 실패가 뜨니까 productDto에 @EqualsAndHashCode 붙여주고 오기
        given(productService.saveProduct(new ProductDto("pen", 5000, 2000)))
                .willReturn(ProductResponseDto.of(12315L, "pen", 5000, 2000));


        ProductDto productDto = new ProductDto("pen", 5000, 2000);
        Gson gson = new Gson(); //구글에서 개발한 JSON 파싱 라이브러리 (Java <-> JSON)
        String content = gson.toJson(productDto);

        mockMvc.perform(
                post("/product")
                        .content(content) //requestBody에 데이터 담기
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").exists()) //각 데이터가 존재하는지 검증
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.price").exists())
                .andExpect(jsonPath("$.stock").exists())
                .andDo(print());

        verify(productService).saveProduct(new ProductDto("pen", 5000, 2000));
    }

}

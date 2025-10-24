package com.springboot.jpa.repository;

import com.springboot.jpa.data.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 📖@DataJpaTest
 *  - JPA와 관련된 설정만 로드해서 테스트 진행
 *  - @Transactional 애너테이션을 포함하고 있어 테스트 코드가 종료되면 자동으로 데이터베이스의 롤백 진행
 *  - 기본적으로 임베디드 데이터베이스 사용 (애플리케이션 안에서 같이 실행되는, 가볍고 별도의 서버 설치가 필요없는 DB)
 *  -> 여기서 ProductRepositort를 정상적으로 주입받을 수 있게 해줌
 */
@DataJpaTest
@Import(ProductRepositoryImpl.class) // 수동 등록 (@DataJpaTest는 JPA관련 빈들만 등록하므로)
public class ProductRepositoryTestByH2 {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void insertTest() {
        //given
        Product product = Product.create(
                "펜", 1000, 1000
        );

        //when
        Product savedProduct = productRepository.insertProduct(product);

        //then
        assertEquals(product.getNumber(), savedProduct.getNumber());
        assertEquals(product.getPrice(), savedProduct.getPrice());
        assertEquals(product.getStock(), savedProduct.getStock());
    }

    @Test
    void selectTest() {
        //given
        Product product = Product.create(
                "펜", 1000, 1000
        );

        /**
         * save() : 엔티티를 영속성 컨텍스트에 저장만 함 (실제 db에 insert쿼리가 날아가는 건 트랜잭션 커밋시점
         *     -> 즉 transaction 커밋이 일어나기 전까지 db에 반영되지 않음
         *
         * saveAndFlush() : save후 즉시 flush(), 즉 트랜잭션이 끝나지 않더라도 DB에 바로반영
         *     -> 여기선 저장후 바로 조회해야하기 때문에 saveAndFlush 사용
         */
        Product savedProduct = productRepository.saveAndFlushProduct(product);

        //when
        Product foundProduct = productRepository.selectProduct(savedProduct.getNumber());

        //then
        assertEquals(product.getName(), foundProduct.getName());
        assertEquals(product.getPrice(), foundProduct.getPrice());
        assertEquals(product.getStock(), foundProduct.getStock());
    }
}

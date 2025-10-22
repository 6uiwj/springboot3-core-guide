package com.springboot.jpa.repository;

import com.springboot.jpa.data.entity.Product;
import com.springboot.jpa.data.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product insertProduct(Product product) {
        Product savedProduct = productJpaRepository.save(product);
        return savedProduct;
    }

    @Override
    public Product selectProduct(Long number) {
        Optional<Product> selectedProduct = productJpaRepository.findById(number);
        if (selectedProduct.isPresent()) {
            Product product = selectedProduct.get();
            return product;
        } else throw new NoSuchElementException();
    }

    @Transactional
    @Override
    public Product updateProduct(Long number, String name) throws Exception {
        Product product = productJpaRepository.findById(number).orElseThrow(NoSuchElementException::new);
        product.updateProduct(name);
        //@Transactional을 붙이면 엔티티의 내용이 변경되면 Dirty Checking 발생 -> 자동으로 save. 즉 save 메서드를 쓸 필요 없음
            //-> 단 @Transactional 은 서비스 레이어에 붙이는게 좋음

        //return productJpaRepository.save(product);
        return product;
    }

    @Override
    public void deleteProduct(Long number) throws Exception {
        Product selectedProduct = productJpaRepository.findById(number).orElseThrow(NoSuchElementException::new);
        productJpaRepository.delete(selectedProduct);
    }
}

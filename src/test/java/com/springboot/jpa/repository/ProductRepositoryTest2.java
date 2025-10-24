package com.springboot.jpa.repository;

import com.springboot.jpa.data.entity.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class ProductRepositoryTest2 {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void basicCRUDTest() {
        /* create */
        Product givenProduct = Product.create("노트", 1000, 500);

        //when
        Product saveProduct = productRepository.insertProduct(givenProduct);

        //then
        Assertions.assertThat(saveProduct.getNumber()).isEqualTo(givenProduct.getNumber());
        Assertions.assertThat(saveProduct.getName()).isEqualTo(givenProduct.getName());
        Assertions.assertThat(saveProduct.getPrice()).isEqualTo(givenProduct.getPrice());
        Assertions.assertThat(saveProduct.getStock()).isEqualTo(givenProduct.getStock());


        /* read */
        //when

        Product selectProduct = productRepository.selectProduct(saveProduct.getNumber());

        //then
        Assertions.assertThat(selectProduct.getNumber()).isEqualTo(saveProduct.getNumber());
        Assertions.assertThat(selectProduct.getName()).isEqualTo(saveProduct.getName());
        Assertions.assertThat(selectProduct.getPrice()).isEqualTo(saveProduct.getPrice());
        Assertions.assertThat(selectProduct.getStock()).isEqualTo(saveProduct.getStock());

        /* Update */
        Product updatedProduct = productRepository.updateProduct(selectProduct.getNumber(),"장난감");

        //then
        assertEquals(updatedProduct.getName(), "장난감");

        /* delete */
        //when
        productRepository.deleteProduct(updatedProduct.getNumber());
        boolean hasProduct = true;
        //then
        try {
            Product deletedProduct = productRepository.selectProduct(selectProduct.getNumber());

        } catch (NoSuchElementException e) {
            hasProduct = false;
        }
        assertFalse(hasProduct);

    }
}

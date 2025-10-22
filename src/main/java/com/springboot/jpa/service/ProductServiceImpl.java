package com.springboot.jpa.service;

import com.springboot.jpa.data.dto.ProductDto;
import com.springboot.jpa.data.dto.ProductResponseDto;
import com.springboot.jpa.data.entity.Product;
import com.springboot.jpa.data.repository.ProductJpaRepository;
import com.springboot.jpa.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final Logger LOGGER  = LoggerFactory.getLogger(ProductServiceImpl.class);

    //private final ProductRepository productRepository;
    private final ProductJpaRepository productJpaRepository;
    private final ProductRepository productRepository;

    @Override
    public ProductResponseDto getProduct(Long number) {
        LOGGER.info("[getProduct] input number {}", number);
        Product product = productJpaRepository.findById(number).get();
        //Product product = productRepository.selectProduct(number);
        LOGGER.info("[getProduct] product number : {}, name : {}", product.getName(), product.getName());

        return ProductResponseDto.from(product);
    }

    @Override
    public ProductResponseDto saveProduct(ProductDto productDto) {
        Product product = productDto.toEntity();
        Product savedProduct = productJpaRepository.save(product);
        //Product savedProduct = productRepository.insertProduct(product);

        LOGGER.info("[saveProduct] saveProduct : {}", savedProduct);

        ProductResponseDto responseDto = ProductResponseDto.from(product);
        return responseDto;
    }

    @Override
    public ProductResponseDto changeProductName(Long number, String name) throws Exception {
        Product foundProduct = productJpaRepository.findById(number).get();
        foundProduct.updateProduct(name);
        Product changedProduct = productJpaRepository.save(foundProduct);
        //Product changedProduct = productRepository.updateProduct(number, name);
        ProductResponseDto responseDto = ProductResponseDto.from(changedProduct);

        return responseDto;
    }

    @Override
    public void deleteProduct(Long number) throws Exception {
        productJpaRepository.deleteById(number);
        //productRepository.deleteProduct(number);
    }
}

package com.springboot.jpa.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@EqualsAndHashCode
@ToString(exclude = "name")
@NoArgsConstructor(access = AccessLevel.PROTECTED) //jpa용
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long number;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

//    private Product(Long number, String name, Integer price, Integer stock, LocalDateTime createdAt, LocalDateTime updatedAt) {
//        this.number = number;
//        this.name = name;
//        this.price = price;
//        this.stock = stock;
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
//    }

    /**
     * 엔티티의 일관성과 캡슐화를 위해
        데이터 유효성과 상태 변경에 대한 책임을 엔티티 내부가 가짐
     */
    //객체 생성
    public static Product create(String name, int price, Integer stock, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Product(null, name, price, stock, createdAt, updatedAt);
    }

    //상태 변경 (setter 대신)
    public void updateProduct(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

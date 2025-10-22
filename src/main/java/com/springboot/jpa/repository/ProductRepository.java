package com.springboot.jpa.repository;

import com.springboot.jpa.data.entity.Product;

/**
 * 체크 예외 vs 언체크 예외
 * 1. 언체크 예외 (RuntimeException - NullPointException, IllegalArgumentException, NoSuchElementException)
 *      - 런타임에만 발생, 처리선택 사항(프로그래밍 실수나 단순 데이터 없음)
 *      - 예) null, 잘못된 인덱스, 잘못된 파라미터
 * 2. 체크 예외 (Exception - IOException, SqlException, FileNotFoundException 등
 *      (RuntimeException 제외))
 *      - 컴파일 시점에 '반드시'처리해야 하는 예외 (예상가능한 문제)
 *      - 파일 I/O, DB, 네트워크
 */
public interface ProductRepository {

    //JPA save에는 이미 예외를 런타임 예외로 던짐 -> 언체크 예외
    Product insertProduct(Product product);

    //데이터 없으면 NosSuchElementException (언체크)
    Product selectProduct(Long number);

    //업데이트 로직에서 검증 실패나 다른 처리 문제 발생 가능 -> 체크 예외
    Product updateProduct(Long number, String name) throws Exception;

    //삭제하려는 엔티티가 존재하지 않거나 DB오류 발생 가능하므로 체크 예외 선언
    void deleteProduct(Long number) throws Exception;
}

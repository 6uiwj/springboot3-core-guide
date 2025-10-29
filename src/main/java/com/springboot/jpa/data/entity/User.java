package com.springboot.jpa.data.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserDetails : UserDetailsService를 통해 입력된 로그인 정보를 가지고 데이터베이스에 사용자 정보를 가져오는 역할
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table
public class User implements UserDetails { //사용자 인증 정보로 사용

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String uid; //사용자 ID

    //JsonProperty(..WRITE_ONLY) : 객체의 직렬화/역직렬화 시 필드를 쓰기 전용으로 지정(입력만 받고, 응답시 포함되지 않음)
    // -> DTO를 거쳐서 요청/응답한다면 필요없음!!
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    //@ElementCollection : 엔티티 안에 있는 기본 타입 컬렉션이나 임베디드 타입 컬렉션을 데이터베이스에 별도의 테이블로 저장하고 싶을 때 사용
    // user_roles이라는 테이블이 별도로 생김
    //@Builder.Default : 빌더로 객체 생성시 null이 들어갈 수 있어서 값이 없어도 null이 아닌 값이 들어아게함(여기선 new ArrayList<>)
    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    /**
     * Collection : 자바에서 객체의 그룹(List, Set 등)을 나타내는 인터페이스\
     * ? extends GrantedAuthority : 상한제한(upper bound) : GrantedAuthority를 상속받은 어떤 객체든 가능
     * stream: 각 요소를 순차적으로 처리할 것임
     * map : 스트림의 각 요소를 다른 객체로 변환할 것임 (SimpleGrantedAuthority 객체로 변환)
     * .collect(Collectors.toList()): 스트림에서 처리한 결과를 다시 리스트로 모음
     *
     * @return GrantedAuthority를 상속받은 객체를 Collection(List나 Set) 형태로 반환할 것임
     */
    //유저가 가진 권한들을 list로 반환하는 메서드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { //userdetails의 메서드 구현
        //roles 리스트의 요소를 하나하나 뽑아서 SimpleGrantedAuthority객체로 변환후 List로 반환할 것임
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    //쓰기전용 (응답에 미포함)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public String getUsername() {
        return this.uid;
    }

    //쓰기전용 (응답에 미포함)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //쓰기전용 (응답에 미포함)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //쓰기전용 (응답에 미포함)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //쓰기전용 (응답에 미포함)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isEnabled() {
        return true;
    }


}

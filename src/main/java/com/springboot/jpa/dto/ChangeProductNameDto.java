package com.springboot.jpa.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeProductNameDto {
    private Long number;
    private String name;
}

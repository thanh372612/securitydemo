package com.phunghung29.securitydemo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePassRequetDto {
    private Long id;
    private String email;
    private String pass_ord;
    private String pass_new;
}

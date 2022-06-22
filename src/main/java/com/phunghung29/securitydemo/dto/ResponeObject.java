package com.phunghung29.securitydemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponeObject {
    private String status;
    private  String mesage;
    private Instant timestamp;
    private  Object data;
}

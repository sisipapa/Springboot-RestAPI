package com.mathflat.sisipapa.dto;

import com.mathflat.sisipapa.enumtype.SchoolType;
import lombok.Data;

@Data
public class StudentDto {

    private String name;
    private int age;
    private SchoolType schoolType;
    private String phoneNumber;


}

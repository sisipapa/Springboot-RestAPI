package com.mathflat.sisipapa.entity;

import com.mathflat.sisipapa.enumtype.SchoolType;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 학생 Entity
 */
@RequiredArgsConstructor
@Data
@Entity(name = "students")
public class Students {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, length = 50, name = "name")
    private String name;

    @Column(nullable = false, name = "age")
    private int age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10, name = "school_type")
    private SchoolType schoolType;

    @Column(nullable = false, length = 15, name = "phone_number", unique=true)
    private String phoneNumber;

    @OneToMany(mappedBy = "students")
    private List<Classes> classes = new ArrayList<>();

    @Builder
    public Students(Long id, String name, int age, SchoolType schoolType, String phoneNumber){
        this.id = id;
        this.name = name;
        this.age = age;
        this.schoolType = schoolType;
        this.phoneNumber = phoneNumber;
    }

}

package com.mathflat.sisipapa.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 과목 Entity
 */
@NoArgsConstructor
@Data
@Entity(name = "subjects")
public class Subjects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String name;

    @OneToMany(mappedBy = "subjects")
    private List<Classes> classes = new ArrayList<>();

    @Builder
    public Subjects(Long id, String name){
        this.id = id;
        this.name = name;
    }
}

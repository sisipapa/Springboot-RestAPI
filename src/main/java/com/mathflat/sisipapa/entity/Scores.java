package com.mathflat.sisipapa.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 점수 Entity
 */
@NoArgsConstructor
@Data
@Entity(name = "scores")
public class Scores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Integer score;

    @OneToOne
    @JoinColumn(name = "classes_id", unique = true)
    private Classes classes;

    @Builder
    public Scores(Long id, Integer score, Classes classes){
        this.id = id;
        this.score = score;
        this.classes = classes;
    }
}

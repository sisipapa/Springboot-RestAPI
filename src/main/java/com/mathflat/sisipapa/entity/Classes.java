package com.mathflat.sisipapa.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 수강 Entity
 */
@NoArgsConstructor
@Data
@Entity(name = "classes")
public class Classes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Students students;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subjects subjects;

    @OneToOne(mappedBy = "classes")
    private Scores scores;

    @Builder
    public Classes(Scores scores, Students students, Subjects subjects){
        this.scores = scores;
        this.students = students;
        this.subjects = subjects;
    }
}

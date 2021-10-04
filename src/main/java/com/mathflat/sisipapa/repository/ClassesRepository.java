package com.mathflat.sisipapa.repository;

import com.mathflat.sisipapa.entity.Classes;
import com.mathflat.sisipapa.entity.Students;
import com.mathflat.sisipapa.entity.Subjects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ClassesRepository extends JpaRepository<Classes, Long> {

    @Transactional
    void deleteByStudents(Students students);

    @Transactional
    void deleteBySubjects(Subjects subjects);

    List<Classes> findByStudents(Students student);

    List<Classes> findBySubjects(Subjects subject);

    Optional<Classes> findByStudentsAndSubjects(Students students, Subjects subject);

}

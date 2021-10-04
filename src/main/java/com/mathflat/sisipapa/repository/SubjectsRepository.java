package com.mathflat.sisipapa.repository;

import com.mathflat.sisipapa.entity.Subjects;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubjectsRepository extends JpaRepository<Subjects, Long> {

    Optional<Subjects> findByName(String name);

}

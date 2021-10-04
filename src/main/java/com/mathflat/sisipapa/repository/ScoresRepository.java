package com.mathflat.sisipapa.repository;

import com.mathflat.sisipapa.entity.Classes;
import com.mathflat.sisipapa.entity.Scores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ScoresRepository extends JpaRepository<Scores, Long> {

    @Transactional
    void deleteByClasses(Classes classes);

    Optional<Scores> findByClasses(Classes classes);

}

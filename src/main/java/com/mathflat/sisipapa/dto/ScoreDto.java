package com.mathflat.sisipapa.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScoreDto {

    private Long id;
    private String name;
    private Integer score;


    @QueryProjection
    public ScoreDto(Long id, String name, Integer score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

}

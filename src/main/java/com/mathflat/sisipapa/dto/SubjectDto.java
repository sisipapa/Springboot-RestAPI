package com.mathflat.sisipapa.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubjectDto {

    private Long id;
    private String name;


    @QueryProjection
    public SubjectDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}

package com.mathflat.sisipapa.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class ResponseDto {

    private Map<String, Object> data;
    private Map<String, String> error;

    @Builder
    public ResponseDto(Map<String, Object> data, Map<String, String> error){
        this.data = data;
        this.error = error;
    }
}

package com.mathflat.sisipapa.controller;

import com.mathflat.sisipapa.dto.ResponseDto;
import com.mathflat.sisipapa.dto.SubjectDto;
import com.mathflat.sisipapa.service.SubjectService;
import com.mathflat.sisipapa.util.ValidateUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping("/subjects")
    @Operation(summary = "과목 추가",
            description = "과목을 등록한다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "과목 등록 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "이미 존재하는 과목입니다. [${subjectName}]", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "과목 이름을 확인하세요.(1~12자, 한글/영어/숫자 포함 가능) [${subjectName}]", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
            })
    public ResponseEntity<? extends ResponseDto> postStudents(@RequestBody SubjectDto dto){

        int httpStatus = 201;
        if(!ValidateUtil.subjectNameValidate(dto.getName())){
            httpStatus = 400;
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "INVALID_SUBJECT_NAME");
            errorMap.put("message", "이름 입력값을 확인하세요.(1~16자, 한글/영어/숫자 포함 가능) [" + dto.getName() + "]");
            return ResponseEntity.status(httpStatus).body(ResponseDto.builder().error(errorMap).build());
        }

        ResponseDto result = subjectService.postSubjects(dto);
        if(Objects.nonNull(result.getError())) httpStatus = 400;

        return ResponseEntity.status(httpStatus).body(result);
    }
    
    @GetMapping("/subjects")
    @Operation(summary = "과목 목록 조회",
            description = "과목 목록을 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "과목 목록조회 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            })
    public ResponseEntity<? extends ResponseDto> getStudents(){
        ResponseDto result = subjectService.getSubjects();
        return ResponseEntity.status(200).body(result);
    }

    @DeleteMapping("/subjects/{subjectId}")
    @Operation(summary = "과목삭제",
            description = "과목을 삭제한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "과목 삭제 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            })
    public ResponseEntity<? extends ResponseDto> deleteSubjects(@PathVariable Long subjectId){
        ResponseDto result = subjectService.deleteSubjects(subjectId);
        return ResponseEntity.status(204).body(result);
    }

    @GetMapping("/subjects/{subjectId}/average-score")
    @Operation(summary = "특정 과목에 대한 전체 학생들의 평균 점수 조회",
            description = "특정 과목에 대한 전체 학생들의 평균 점수 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "특정 과목에 대한 전체 학생들의 평균 점수 조회 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "과목을 찾을 수 없습니다. [${subjectId}]", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
            })
    public ResponseEntity<? extends ResponseDto> getAverage(@PathVariable Long subjectId){

        int httpStatus = 200;

        ResponseDto result = subjectService.getAverage(subjectId);
        if(Objects.nonNull(result.getError())) httpStatus = 404;

        return ResponseEntity.status(httpStatus).body(result);
    }

}

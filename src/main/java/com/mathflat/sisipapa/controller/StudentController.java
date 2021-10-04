package com.mathflat.sisipapa.controller;

import com.mathflat.sisipapa.dto.ResponseDto;
import com.mathflat.sisipapa.dto.ScoreDto;
import com.mathflat.sisipapa.dto.StudentDto;
import com.mathflat.sisipapa.dto.SubjectDto;
import com.mathflat.sisipapa.service.StudentService;
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
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/students")
    @Operation(summary = "학생등록",
            description = "학생 정보를 등록한다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "학생 등록 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "이미 존재하는 학생입니다. [${studentPhoneNumber}]", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "이름 입력값을 확인하세요.(1~16자, 한글/영어/숫자 포함 가능) [${name}]", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "나이 입력값을 확인하세요.(8~19 값만 유효) [${age}]", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "학교급 입력값을 확인하세요.(ELEMANTARY, MIDDLE, HIGH 값만 유효) [${schoolType}]", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "전화번호 입력값을 확인하세요.(000-0000-0000 형식) [${phoneNumber}]", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
            })
    public ResponseEntity<? extends ResponseDto> postStudents(@RequestBody StudentDto dto){

        int httpStatus = 201;

        if(!ValidateUtil.studentNameValidate(dto.getName())){
            httpStatus = 400;
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "INVALID_STUDENT_NAME");
            errorMap.put("message", "이름 입력값을 확인하세요.(1~16자, 한글/영어/숫자 포함 가능) [" + dto.getName() + "]");
            return ResponseEntity.status(httpStatus).body(ResponseDto.builder().error(errorMap).build());
        }

        if(!ValidateUtil.studentAgeValidate(dto.getAge())){
            httpStatus = 400;
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "INVALID_STUDENT_AGE");
            errorMap.put("message", "나이 입력값을 확인하세요.(8~19 값만 유효) [" + dto.getAge() + "]");
            return ResponseEntity.status(httpStatus).body(ResponseDto.builder().error(errorMap).build());
        }

        if(!ValidateUtil.studentSchoolTypeValidate(dto.getSchoolType())){
            httpStatus = 400;
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "INVALID_STUDENT_SCOOLTYPE");
            errorMap.put("message", "학교급 입력값을 확인하세요.(ELEMANTARY, MIDDLE, HIGH 값만 유효) [" + dto.getSchoolType().name() + "]");
            return ResponseEntity.status(httpStatus).body(ResponseDto.builder().error(errorMap).build());
        }

        if(!ValidateUtil.studentPhoneNumberValidate(dto.getPhoneNumber())){
            httpStatus = 400;
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "INVALID_STUDENT_PHONENUMBER");
            errorMap.put("message", "전화번호 입력값을 확인하세요.(000-0000-0000 형식) [" + dto.getPhoneNumber() + "]");
            return ResponseEntity.status(httpStatus).body(ResponseDto.builder().error(errorMap).build());
        }

        ResponseDto result = studentService.postStudents(dto);
        if(Objects.nonNull(result.getError())) httpStatus = 400;

        return ResponseEntity.status(httpStatus).body(result);
    }

    @GetMapping("/students")
    @Operation(summary = "학생조회",
            description = "학생 목록을 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "학생 조회 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
            })
    public ResponseEntity<? extends ResponseDto> getStudents(){
        ResponseDto result = studentService.getStudents();
        return ResponseEntity.status(200).body(result);
    }

    @DeleteMapping("/students/{studentId}")
    @Operation(summary = "학생삭제",
            description = "학생을 삭제한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "학생 삭제 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
            })
    public ResponseEntity<? extends ResponseDto> deleteStudents(@PathVariable Long studentId){
        ResponseDto result = studentService.deleteStudents(studentId);
        return ResponseEntity.status(204).body(result);
    }

    @PostMapping("/students/{studentId}/subjects/{subjectId}/scores")
    @Operation(summary = "특정 학생, 특정 과목에 점수 할당",
            description = "특정 학생, 특정 과목에 점수 할당한다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "특정 학생, 특정 과목에 점수 할당 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "학생을 찾을 수 없습니다. [${studentId}]", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "과목을 찾을 수 없습니다. [${subjectId}]", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "수강 과목을 찾을 수 없습니다. [${studentId}, ${subjectId}]", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
            })
    public ResponseEntity<? extends ResponseDto> postScore(@PathVariable Long studentId, @PathVariable Long subjectId, @RequestBody ScoreDto dto){

        int httpStatus = 201;

        ResponseDto result = studentService.postScore(studentId, subjectId, dto.getScore());
        if(Objects.nonNull(result.getError())) httpStatus = 404;

        return ResponseEntity.status(httpStatus).body(result);
    }

    @PutMapping("/students/{studentId}/subjects/{subjectId}/scores")
    @Operation(summary = "특정 학생, 특정 과목의 점수 수정",
            description = "특정 학생, 특정 과목의 점수 수정한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "특정 학생, 특정 과목의 점수 수정 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "학생을 찾을 수 없습니다. [${studentId}]", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "과목을 찾을 수 없습니다. [${subjectId}]", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "수강 과목을 찾을 수 없습니다. [${studentId}, ${subjectId}]", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "점수를 찾을 수 없습니다. [${subjectId}, ${subjectId}]", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
            })
    public ResponseEntity<? extends ResponseDto> putScore(@PathVariable Long studentId, @PathVariable Long subjectId, @RequestBody ScoreDto dto){

        int httpStatus = 200;

        ResponseDto result = studentService.putScore(studentId, subjectId, dto.getScore());
        if(Objects.nonNull(result.getError())) httpStatus = 404;

        return ResponseEntity.status(httpStatus).body(result);
    }

    @DeleteMapping("/students/{studentId}/subjects/{subjectId}/scores")
    @Operation(summary = "특정 학생, 특정 과목의 점수 삭제",
            description = "특정 학생, 특정 과목의 점수 삭제한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "특정 학생, 특정 과목의 점수 삭제 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "학생을 찾을 수 없습니다. [${studentId}]", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "과목을 찾을 수 없습니다. [${subjectId}]", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "수강 과목을 찾을 수 없습니다. [${studentId}, ${subjectId}]", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
            })
    public ResponseEntity<? extends ResponseDto> deleteScore(@PathVariable Long studentId, @PathVariable Long subjectId){

        int httpStatus = 204;

        ResponseDto result = studentService.deleteScore(studentId, subjectId);
        if(Objects.nonNull(result.getError())) httpStatus = 404;

        return ResponseEntity.status(httpStatus).body(result);
    }

    @GetMapping("/students/{studentId}/average-score")
    @Operation(summary = "특정 학생의 평균 점수 조회",
            description = "특정 학생의 평균 점수 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "특정 학생의 평균 점수 조회 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "학생을 찾을 수 없습니다. [${studentId}]", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
            })
    public ResponseEntity<? extends ResponseDto> getAverage(@PathVariable Long studentId){

        int httpStatus = 200;

        ResponseDto result = studentService.getAverage(studentId);
        if(Objects.nonNull(result.getError())) httpStatus = 404;

        return ResponseEntity.status(httpStatus).body(result);
    }
}

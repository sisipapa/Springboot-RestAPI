package com.mathflat.sisipapa.service;

import com.mathflat.sisipapa.dto.ResponseDto;
import com.mathflat.sisipapa.dto.ScoreDto;
import com.mathflat.sisipapa.dto.StudentDto;
import com.mathflat.sisipapa.dto.SubjectDto;
import com.mathflat.sisipapa.entity.Classes;
import com.mathflat.sisipapa.entity.Scores;
import com.mathflat.sisipapa.entity.Students;
import com.mathflat.sisipapa.entity.Subjects;
import com.mathflat.sisipapa.repository.ClassesRepository;
import com.mathflat.sisipapa.repository.ScoresRepository;
import com.mathflat.sisipapa.repository.StudentsRepository;
import com.mathflat.sisipapa.repository.SubjectsRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.mathflat.sisipapa.entity.QClasses.classes;
import static com.mathflat.sisipapa.entity.QScores.scores;
import static com.mathflat.sisipapa.entity.QStudents.students;
import static com.mathflat.sisipapa.entity.QSubjects.subjects;

@RequiredArgsConstructor
@Service
public class StudentService {

    private final StudentsRepository studentRepository;

    private final SubjectsRepository subjectsRepository;

    private final ClassesRepository classesRepository;

    private final ScoresRepository scoresRepository;

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 학생등록
     * @param dto
     * @return
     */
    public ResponseDto postStudents(StudentDto dto){

        // DB Column에 Unique Index를 걸어두었지만 Application 에서 중복여부 한번 더 체크
        Students findStudents = studentRepository.findByPhoneNumber(dto.getPhoneNumber()).orElse(null);
        if(Objects.nonNull(findStudents)){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "ALREADY_EXIST_STUDENT");
            errorMap.put("message", "이미 존재하는 학생입니다. [" + dto.getPhoneNumber() + "]");
            return ResponseDto.builder().error(errorMap).build();
        }

        // 클라이언트에게 전달받은 데이터 Students Entity 형식에 맞게 Set
        Students students = Students.builder()
                .name(dto.getName())
                .age(dto.getAge())
                .schoolType(dto.getSchoolType())
                .phoneNumber(dto.getPhoneNumber())
                .build();
        Students saveStudent = studentRepository.save(students);

        // 학생 추가시 추가한 학생은 등록되어 있는 모든 과목을 수강하여야만 합니다.
        // 1. 과목조회
        List<Subjects> subjects = subjectsRepository.findAll();
        // 2. 수강신청
        List<Classes> classes = new ArrayList<>();
        subjects.stream().forEach(findSubject -> {

            Classes newClass = Classes.builder()
                    .students(Students.builder().id(saveStudent.getId()).build())
                    .subjects(Subjects.builder().id(findSubject.getId()).build())
                    .build();
            classesRepository.save(newClass);

        });

        return ResponseDto.builder().data(null).error(null).build();
    }

    /**
     * 학생 목록 조회
     * @return
     */
    public ResponseDto getStudents(){

        List<StudentDto> result = jpaQueryFactory
                .select(Projections.bean(StudentDto.class,
                        students.id,
                        students.name,
                        students.age,
                        students.schoolType,
                        students.phoneNumber))
                .from(students)
                .fetch();
        Map<String,Object> dataMap = Map.of("students", result);

        return ResponseDto.builder().data(dataMap).error(null).build();
    }

    /**
     * 학생삭제
     * @param studentId
     * @return
     */
    public ResponseDto deleteStudents(Long studentId){

        Students student = Students.builder().id(studentId).build();
        // 학생의 수업 모두 조회
        List<Classes> classes = classesRepository.findByStudents(student);
        classes.stream().forEach(findClass -> {
            // score(점수)
            scoresRepository.deleteByClasses(findClass);
        });

        // classes(수업) 삭제
        classesRepository.deleteByStudents(student);
        // students(학생) 삭제
        studentRepository.delete(student);

        return ResponseDto.builder().data(null).error(null).build();

    }

    /**
     * 특정 학생, 특정 과목에 점수 할당
     * @param studentId
     * @param subjectId
     * @param score
     * @return
     */
    public ResponseDto postScore(Long studentId, Long subjectId, Integer score){
        
        // 학생 존재여부 확인
        Students student = studentRepository.findById(studentId).orElse(null);
        if(Objects.isNull(student)){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "STUDENT_NOT_FOUND");
            errorMap.put("message", "학생을 찾을 수 없습니다. [" + studentId + "]");
            return ResponseDto.builder().error(errorMap).build();
        }
        
        // 과목 존재여부 확인
        Subjects subject = subjectsRepository.findById(subjectId).orElse(null);
        if(Objects.isNull(subject)){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "SUBJECT_NOT_FOUND");
            errorMap.put("message", "과목을 찾을 수 없습니다. [" + subjectId + "]");
            return ResponseDto.builder().error(errorMap).build();
        }

        // 수강 과목 존재여부 확인
        Classes findClass = classesRepository.findByStudentsAndSubjects(student, subject).get();
        if(Objects.isNull(findClass)){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "CLASS_NOT_FOUND");
            errorMap.put("message", "수강 과목을 찾을 수 없습니다. [" + studentId + ", " + subjectId + "]");
            return ResponseDto.builder().error(errorMap).build();
        }

        // 수강 과목에 등록된 점수가 있는지 여부 확인
        Scores scores = scoresRepository.findByClasses(findClass).orElse(Scores.builder().score(score).classes(findClass).build());
        
        // 점수 등록
        scoresRepository.save(scores);
        
        return ResponseDto.builder().data(null).error(null).build();
    }

    /**
     * 특정 학생, 특정 과목의 점수 수정
     * @param studentId
     * @param subjectId
     * @param score
     * @return
     */
    public ResponseDto putScore(Long studentId, Long subjectId, Integer score){

        // 학생 존재여부 확인
        Students student = studentRepository.findById(studentId).orElse(null);
        if(Objects.isNull(student)){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "STUDENT_NOT_FOUND");
            errorMap.put("message", "학생을 찾을 수 없습니다. [" + studentId + "]");
            return ResponseDto.builder().error(errorMap).build();
        }

        // 과목 존재여부 확인
        Subjects subject = subjectsRepository.findById(subjectId).orElse(null);
        if(Objects.isNull(subject)){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "SUBJECT_NOT_FOUND");
            errorMap.put("message", "과목을 찾을 수 없습니다. [" + subjectId + "]");
            return ResponseDto.builder().error(errorMap).build();
        }
        
        // 수강 과목 존재여부 확인
        Classes findClass = classesRepository.findByStudentsAndSubjects(student, subject).get();
        if(Objects.isNull(findClass)){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "CLASS_NOT_FOUND");
            errorMap.put("message", "수강 과목을 찾을 수 없습니다. [" + studentId + ", " + subjectId + "]");
            return ResponseDto.builder().error(errorMap).build();
        }

        // 점수 존재여부 확인
        Scores findScore = scoresRepository.findByClasses(findClass).orElse(null);
        if(Objects.isNull(findScore)){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "SCORE_NOT_FOUND");
            errorMap.put("message", "점수를 찾을 수 없습니다. [" + studentId + ", " + subjectId + "]");
            return ResponseDto.builder().error(errorMap).build();
        }
        
        // 점수 수정
        findScore.setScore(score);
        scoresRepository.save(findScore);

        return ResponseDto.builder().data(null).error(null).build();
    }

    /**
     * 특정 학생, 특정 과목의 점수 삭제
     * @param studentId
     * @param subjectId
     * @return
     */
    public ResponseDto deleteScore(Long studentId, Long subjectId){

        // 학생 존재여부 확인
        Students student = studentRepository.findById(studentId).orElse(null);
        if(Objects.isNull(student)){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "STUDENT_NOT_FOUND");
            errorMap.put("message", "학생을 찾을 수 없습니다. [" + studentId + "]");
            return ResponseDto.builder().error(errorMap).build();
        }

        // 과목 존재여부 확인
        Subjects subject = subjectsRepository.findById(subjectId).orElse(null);
        if(Objects.isNull(subject)){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "SUBJECT_NOT_FOUND");
            errorMap.put("message", "과목을 찾을 수 없습니다. [" + subjectId + "]");
            return ResponseDto.builder().error(errorMap).build();
        }

        // 수강 과목 존재여부 확인
        Classes findClass = classesRepository.findByStudentsAndSubjects(student, subject).get();
        if(Objects.isNull(findClass)){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "CLASS_NOT_FOUND");
            errorMap.put("message", "수강 과목을 찾을 수 없습니다. [" + studentId + ", " + subjectId + "]");
            return ResponseDto.builder().error(errorMap).build();
        }

        // 점수 삭제
        scoresRepository.deleteByClasses(findClass);

        return ResponseDto.builder().data(null).error(null).build();
    }

    /**
     * 특정 학생의 평균 점수 조회
     * @param studentId
     * @return
     */
    public ResponseDto getAverage(Long studentId){

        // 학생 존재여부 확인
        Students student = studentRepository.findById(studentId).orElse(null);
        if(Objects.isNull(student)){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "STUDENT_NOT_FOUND");
            errorMap.put("message", "학생을 찾을 수 없습니다. [" + studentId + "]");
            return ResponseDto.builder().error(errorMap).build();
        }
        
        // 학생 수강 과목조회
        List<ScoreDto> subjectList = jpaQueryFactory
                .select(
                    Projections.bean(
                            ScoreDto.class,
                            subjects.id,
                            subjects.name,
                            scores.score
                    )
                )
                .from(subjects)
                .join(subjects.classes, classes)
                .join(classes.scores, scores)
                .where(classes.students.id.eq(studentId))
                .fetch();

        // 학생 전체과목 평균조회
        Double averageScore = subjectList.stream()
                .mapToInt(subject -> subject.getScore())
                .average().orElse(-1);

        Map<String, Object> dataMap = new HashMap<>() {{
            put("averageScore", averageScore);
            put("subjects", subjectList);
        }};

        return ResponseDto.builder().data(dataMap).error(null).build();
    }
}

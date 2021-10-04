package com.mathflat.sisipapa.service;

import com.mathflat.sisipapa.dto.ResponseDto;
import com.mathflat.sisipapa.dto.ScoreDto;
import com.mathflat.sisipapa.dto.StudentDto;
import com.mathflat.sisipapa.dto.SubjectDto;
import com.mathflat.sisipapa.entity.Classes;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.mathflat.sisipapa.entity.QClasses.classes;
import static com.mathflat.sisipapa.entity.QScores.scores;
import static com.mathflat.sisipapa.entity.QStudents.students;
import static com.mathflat.sisipapa.entity.QSubjects.subjects;

@RequiredArgsConstructor
@Service
public class SubjectService {

    private final StudentsRepository studentRepository;

    private final SubjectsRepository subjectsRepository;

    private final ClassesRepository classesRepository;

    private final ScoresRepository scoresRepository;

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 과목등록
     * @param dto
     * @return
     */
    public ResponseDto postSubjects(SubjectDto dto){

        // DB Column에 Unique Index를 걸어두었지만 Application 에서 중복여부 한번 더 체크
        Subjects findSubjects = subjectsRepository.findByName(dto.getName()).orElse(null);
        if(Objects.nonNull(findSubjects)){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "ALREADY_EXIST_SUBJECT");
            errorMap.put("message", "이미 존재하는 과목입니다. [" + dto.getName() + "]");
            return ResponseDto.builder().error(errorMap).build();
        }

        // 클라이언트에게 전달받은 데이터 Subjects Entity 형식에 맞게 Set
        Subjects subjects = Subjects.builder()
                .name(dto.getName())
                .build();
        Subjects saveSubjects = subjectsRepository.save(subjects);

        // 과목 추가시 등록되어 있는 학생은 모두 추가한 과목을 수강해야만 합니다.
        List<Students> students = studentRepository.findAll();
        students.stream().forEach(findStudent -> {
            Classes newClass = Classes.builder()
                    .students(Students.builder().id(findStudent.getId()).build())
                    .subjects(Subjects.builder().id(saveSubjects.getId()).build())
                    .build();
            classesRepository.save(newClass);
        });

        return ResponseDto.builder().data(null).error(null).build();
    }

    /**
     * 과목 목록 조회
     * @return
     */
    public ResponseDto getSubjects(){

        List<SubjectDto> result = jpaQueryFactory
                .select(Projections.bean(SubjectDto.class,
                        subjects.id,
                        subjects.name))
                .from(subjects)
                .fetch();
        Map<String,Object> dataMap = Map.of("subjects", result);

        return ResponseDto.builder().data(dataMap).error(null).build();
    }

    /**
     * 과목 삭제
     * @param subjectId
     * @return
     */
    public ResponseDto deleteSubjects(Long subjectId){
        Subjects subject = Subjects.builder().id(subjectId).build();

        // 과목의 수업 모두 조회
        List<Classes> classes = classesRepository.findBySubjects(subject);
        classes.stream().forEach(findClass -> {
            // score(점수)
            scoresRepository.deleteByClasses(findClass);
        });

        // classes(수업) 삭제
        classesRepository.deleteBySubjects(subject);
        // subjects(과목) 삭제
        subjectsRepository.delete(subject);
        return ResponseDto.builder().data(null).error(null).build();

    }

    /**
     * 특정 과목에 대한 전체 학생들의 평균 점수 조회
     * @param subjectId
     * @return
     */
    public ResponseDto getAverage(Long subjectId){

        // 과목 존재여부 확인
        Subjects subject = subjectsRepository.findById(subjectId).orElse(null);
        if(Objects.isNull(subject)){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "SUBJECT_NOT_FOUND");
            errorMap.put("message", "과목을 찾을 수 없습니다. [" + subjectId + "]");
            return ResponseDto.builder().error(errorMap).build();
        }

        // 과목 수강 학생조회
        List<ScoreDto> studentsList = jpaQueryFactory
                .select(
                        Projections.bean(
                                ScoreDto.class,
                                students.id,
                                students.name,
                                scores.score
                        )
                )
                .from(students)
                .join(students.classes, classes)
                .join(classes.scores, scores)
                .where(classes.subjects.id.eq(subjectId))
                .fetch();

        // 과목 전체학생 평균조회
        Double averageScore = studentsList.stream()
                .mapToInt(student -> student.getScore())
                .average().orElse(-1);

        Map<String, Object> dataMap = new HashMap<>() {{
            put("averageScore", averageScore);
            put("students", studentsList);
        }};

        return ResponseDto.builder().data(dataMap).error(null).build();
    }
}

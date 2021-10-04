# 1. 실행 방법 및 코드 및 구조에 대한 개요  
## 1-1. 실행방법
### IDE 툴에서 실행(Intellij 기준)  
1. checkout : https://github.com/sisipapa/Springboot-RestAPI.git
2. 화면 우측 Gradle > Tasks > build > clean
3. Services Springboot 서버 실행
   <img src="https://sisipapa.github.io/assets/images/posts/service-springboot.PNG" >  
4. 서버가 구동되면 아래 두가지 방법 중 하나를 선택해서 테스트 진행
- api-requests.http 파일에서 테스트 진행
- http://localhost:8080/swagger-ui.html

### jar 파일 직접 실행
1. checkout : https://github.com/mathflat-dev/20210916_sisipapa.git
2. console 창에서 excuteJar 디렉토리로 이동 후 sisipapa.jar파일 실행 : java -jar sisipapa
3. 서버가 구동되면 아래 두가지 방법 중 하나를 선택해서 테스트 진행
- api-requests.http 파일에서 테스트 진행
- http://localhost:8080/swagger-ui.html

## 1-2. 코드 및 구조에 대한 개요
|디렉토리|파일명|설명|
|:---|:---|:---|
|config|OpenApiConfig.java|Swagger 관련 초기 설정|
|config|QuerydslConfig.java|JPAQueryFactory를 편하게 주입해서 사용하기 위한 Bean등록 설정|
|controller|StudentController.java|students 관련 Http 요청 처리 Controller|
|controller|SubjectController.java|subjects 관련 Http 요청 처리 Controller|
|dto|ResponseDto.java|응답 결과 Dto|
|dto|ScoreDto.java|점수 Dto|
|dto|StudentDto.java|학생 Dto|
|dto|SubjectDto.java|과목 Dto|
|entity|Classes.java|수강 Entity|
|entity|Scores.java|점수 Entity|
|entity|Students.java|학생 Entity|
|entity|Subjects.java|과목 Entity|
|enumtype|SchoolType.java|학교급을 정의한 Enum Class|
|repository|ClassesRepository.java|수강 DB 처리를 위한 Repository|
|repository|ScoresRepository.java|점수 DB 처리를 위한 Repository|
|repository|StudentsRepository.java|학생 DB 처리를 위한 Repository|
|repository|SubjectsRepository.java|과목 DB 처리를 위한 Repository|
|service|StudentService.java|학생 관련 비지니스 처리를 위한 Service|
|service|SubjectService.java|과목 관련 비지니스 처리를 위한 Service|  
|util|ValidateUtil.java|입력 값들에 대한 유효성 체크|  
  
- H2 Console 접속 - http://localhost:8080/h2 접속 후 connect 버튼을 클릭하면 DB 스키마 및 데이터 확인이 가능합니다.  
- Swagger Document 접속 - http://localhost:8080/swagger-ui.html  
- initDb.java 서버구동시 초기 과목 초기 데이터를 3건 등록해 줍니다. 학생 추가시 추가한 학생은 등록되어 있는 모든 과목을 수강하는 기능 테스트를 위한 초기 데이터 입니다.



## 1-3. 개발환경
- java : jdk 11  
- Framework : Springboot 2.5.4  
- skill set : spring data jpa, querydsl, swagger, gradle
- DB : h2(mysql)  

# 2. 디비 스키마에 대해서도 제출해 주세요. (ERD 혹은 DDL)  
## 2-1. ERD
URL : https://aquerytool.com/aquerymain/index/?rurl=e2a199d8-7572-48a4-927f-a324c5d6b883&  
Password : kd7725  

URL이 열리지 않을 수도 있을 것 같아 아래 이미지로도 첨부합니다.  
<img src="https://sisipapa.github.io/assets/images/posts/erd.PNG" >  

## 2-2. DDL
```sql
CREATE TABLE students
(
    `id`           BIGINT         NOT NULL    AUTO_INCREMENT COMMENT '학생 식별자', 
    `name`         VARCHAR(50)    NOT NULL    COMMENT '학생 이름', 
    `age`          TINYINT(2)     NOT NULL    COMMENT '학생 나이', 
    `schoolType`   VARCHAR(10)    NOT NULL    COMMENT '학생 학교급', 
    `phoneNumber`  VARCHAR(15)    NOT NULL    COMMENT '학생 전화번호', 
    CONSTRAINT PK_students PRIMARY KEY (id)
);

ALTER TABLE students COMMENT '학생';

CREATE TABLE classes
(
   `id`          BIGINT    NOT NULL    AUTO_INCREMENT COMMENT '수강 식별자',
   `student_id`  BIGINT    NOT NULL    COMMENT '학생 식별자',
   `subject_id`  BIGINT    NOT NULL    COMMENT '과목 식별자',
   CONSTRAINT PK_scores PRIMARY KEY (id)
);

ALTER TABLE classes COMMENT '수업';

ALTER TABLE classes
   ADD CONSTRAINT FK_classes_student_id_students_id FOREIGN KEY (student_id)
      REFERENCES students (id) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE classes
   ADD CONSTRAINT FK_classes_subject_id_subjects_id FOREIGN KEY (subject_id)
      REFERENCES subjects (id) ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE TABLE subjects
(
    `id`    BIGINT          NOT NULL    AUTO_INCREMENT COMMENT '과목 식별자',
    `name`  VARCHAR(100)    NOT NULL    COMMENT '과목 이름',
    CONSTRAINT PK_subjects PRIMARY KEY (id)
);

ALTER TABLE subjects COMMENT '과목';

CREATE TABLE scores
(
   `id`          BIGINT     NOT NULL    AUTO_INCREMENT COMMENT '점수 식별자',
   `score`       INTEGER    NULL        COMMENT '점수',
   `classes_id`  BIGINT     NOT NULL    COMMENT '수강 식별자',
   PRIMARY KEY (id)
);

ALTER TABLE scores COMMENT '점수';

ALTER TABLE scores
   ADD CONSTRAINT FK_scores_classes_id_classes_id FOREIGN KEY (classes_id)
      REFERENCES classes (id) ON DELETE RESTRICT ON UPDATE RESTRICT;
```  




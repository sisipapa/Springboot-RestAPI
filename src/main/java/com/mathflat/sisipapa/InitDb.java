package com.mathflat.sisipapa;

import com.mathflat.sisipapa.entity.Subjects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;

        /**
         * 학생이 등록되면 자동으로 모든 과목 수강신청이 되어야 한다는 내용 테스트를 위해 3개 과목 초기값 세팅 
         */
        public void dbInit() {
            IntStream.rangeClosed(1,3).forEach(index ->{
                Subjects subjects = new Subjects();
                subjects.setName("subject-" + index);
                em.persist(subjects);
            });
        }
    }
}

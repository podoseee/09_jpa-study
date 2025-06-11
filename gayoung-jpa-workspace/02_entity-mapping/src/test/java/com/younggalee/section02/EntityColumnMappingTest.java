package com.younggalee.section02;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.Date;

public class EntityColumnMappingTest {
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @BeforeAll
    public static void initEntityManagerFactory() { entityManagerFactory = Persistence.createEntityManagerFactory("jpa_test"); }

    @BeforeEach
    public void initEntityManager() { entityManager = entityManagerFactory.createEntityManager(); }

    @AfterEach
    public void destroyEntityManager() { entityManager.close(); }

    @AfterAll
    public static void destroyEntityManagerFactory() { entityManagerFactory.close(); }

    @Test
    public void 컬럼_매핑_테스트() {
        User user = User.builder()
                .userNo(1)
                .userId("admin01")
                .userPwd("1234")
                .nickName("관리자")
                .phone("010-1234-1234")
                .address("서울시 강서구")
                .email("admin@gmail.com")
                .enrollDate(new Date())
                .userRole("ADMIN")
                .status("Y")
                .build();


        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {
            entityManager.persist(user); // 비영속상태인 user를 컨텍스트에 저장
            transaction.commit(); // 커밋
        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
        }
    }

}

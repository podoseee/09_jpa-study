package com.sotogito.section02;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.Date;

public class EntityColumMappingTest {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @BeforeAll
    static void initEntityManagerFactory() {
        entityManagerFactory = Persistence.createEntityManagerFactory("jpa_test");
    }

    @BeforeEach
    void initEntityManager() {
        entityManager = entityManagerFactory.createEntityManager();
    }

    @AfterEach
    void distroyEntityManager() {
        entityManager.close();
    }

    @AfterAll
    static void destroyEntityManagerFactory() {
        entityManagerFactory.close();
    }

    @Test
    void 컬럼_매핑_테스트() {
        User user1 = User.builder()
                .userId("admin01")
                .userPwd("1234")
                .nickname("관리자")
                .phone("010-1234-5678")
                .email("admin01@gmail.com")
                .address("서울")
                .enrollDate(LocalDateTime.now())
                .userRole("ADMIN")
                .status("Y")
                .build();

        User user2 = User.builder()
                .userId("admin02")
                .userPwd("5678")
                .nickname("부관리자")
                .phone("010-1234-9999")
                .email("admin02@gmail.com")
                .address("부산")
                .enrollDate(LocalDateTime.now().plusSeconds(1))
                .userRole("ADMIN")
                .status("Y")
                .build();

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.persist(user1);
            entityManager.persist(user2);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

}

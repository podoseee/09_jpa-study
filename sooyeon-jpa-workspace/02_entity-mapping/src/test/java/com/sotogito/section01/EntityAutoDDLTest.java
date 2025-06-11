package com.sotogito.section01;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

public class EntityAutoDDLTest {

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
    void 테이블_자동_생성_테스트() {
        /**
         *     create table Test1 (
         *         testNo integer not null,
         *         testDate datetime(6),
         *         testName varchar(255),
         *         primary key (testNo)
         *     ) engine=InnoDB
         */
    }

}

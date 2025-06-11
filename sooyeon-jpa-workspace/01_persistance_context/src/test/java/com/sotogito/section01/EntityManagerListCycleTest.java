package com.sotogito.section01;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

class EntityManagerListCycleTest {

    private static EntityManagerFactory entityManagerFactory; /// 한번만 생성 (SqlSessionFactory같은)
    private EntityManager entityManager; /// 매번 생성 (SqlSession같은)

    @BeforeAll
    static void initEntityManagerFactory() {
        entityManagerFactory = Persistence.createEntityManagerFactory("jpa_test");
    }

    @BeforeEach
    void initEntityManager() {
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Test
    void 요청1() {
        System.out.println("요청1");
        System.out.println("EntityManagerFactory.hasCode: "+ entityManagerFactory.hashCode()); // 900824070
        System.out.println("EntityManager.hasCode: "+ entityManager.hashCode());               // 1189187821
    }

    @Test
    void 요청2() {
        System.out.println("요청2");
        System.out.println("EntityManagerFactory.hasCode: "+ entityManagerFactory.hashCode()); // 900824070
        System.out.println("EntityManager.hasCode: "+ entityManager.hashCode());               // 233315297
    }

    @AfterEach
    void distroyEntityManager() {
        entityManager.close();
    }

    @AfterAll
    static void destroyEntityManagerFactory() {
        entityManagerFactory.close();
    }

}
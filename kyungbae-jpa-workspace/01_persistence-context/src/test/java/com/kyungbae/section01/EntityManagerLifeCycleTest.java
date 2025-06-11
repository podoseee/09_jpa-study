package com.kyungbae.section01;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

class EntityManagerLifeCycleTest {

    private static EntityManagerFactory entityManagerFactory; // EntityManager 생성을 위한 인스턴스
    private EntityManager entityManager; // 엔티티를 관리하는 인스턴스, 엔티티 CRUD관련 일 진행

    @BeforeAll // 모든 테스트 동작 전 최초 1회 실행
    public static void initEntityManagerFactory(){
        // EntityManagerFactory (싱글톤 관리) 생성
        entityManagerFactory = Persistence.createEntityManagerFactory("jpa_test");
    }

    @BeforeEach // 테스트 동작 전 매번 실행
    public void initEntityManager(){
        // EntityManager 생성
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Test
    public void 요청1(){
        System.out.println("테스트 메소드 1");
        System.out.println("EntityManagerFactory.hashCode: " + entityManagerFactory.hashCode());
        System.out.println("EntityManager.hashCode" + entityManager.hashCode());
    }

    @Test
    public void 요청2(){
        System.out.println("테스트 메소드 2");
        System.out.println("EntityManagerFactory.hashCode: " + entityManagerFactory.hashCode());
        System.out.println("EntityManager.hashCode" + entityManager.hashCode());
    }

    @AfterEach // 테스트 후 매번 실행
    public void destroyEntityManager(){
        // EntityManager 소멸
        entityManager.close();
    }

    @AfterAll
    public static void destroyEntityManagerFactory(){
        // EntityManagerFactory 소멸
        entityManagerFactory.close();
    }

}
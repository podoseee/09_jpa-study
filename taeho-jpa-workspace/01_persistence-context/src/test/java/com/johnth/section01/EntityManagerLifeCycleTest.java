package com.johnth.section01;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

public class EntityManagerLifeCycleTest {

    private static EntityManagerFactory entityManagerFactory; // EntityManager 생성을 위한 인스턴스, 싱글톤으로 관리하는걸 권장
    private EntityManager entityManager; // 엔티티를 관리하는 인스턴스, 엔티티 저장/수정/삭제/조회 와 관련된 일 진행

    @BeforeAll // 모든 테스트 동작 전에 최초에 한번만 실행 (즉, 테스트 클래스가 동작하기 이전)
    public static void initEntityManagerFactory(){
        // EntityManagerFactory (싱글톤 관리) 생성
        entityManagerFactory = Persistence.createEntityManagerFactory("jpa_test");
    }

    @BeforeEach // 테스트 동작 전에 매번 실행 (즉, 테스트 메소드가 동작하기 이전)
    public void initEntityManager(){
        // EntityManager 생성
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Test
    public void 요청1(){
        System.out.println("테스트 메소드 1");
        System.out.println("EntityManagerFactory.hashCode: " + entityManagerFactory.hashCode());
        System.out.println("EntityManager.hashCode: " + entityManager.hashCode());
    }

    @Test
    public void 요청2(){
        System.out.println("테스트 메소드 2");
        System.out.println("EntityManagerFactory.hashCode: " + entityManagerFactory.hashCode());
        System.out.println("EntityManager.hashCode: " + entityManager.hashCode());
    }

    @AfterEach // 테스트 동작 후에 매번 실행
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

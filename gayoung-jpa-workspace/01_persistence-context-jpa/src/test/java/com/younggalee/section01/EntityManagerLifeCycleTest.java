package com.younggalee.section01;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

public class EntityManagerLifeCycleTest {
    //전역필드로
    private static EntityManagerFactory entityManagerFactory; // 매니저 생성을 위한 인스턴스, 싱글톤으로 관리하는 걸 권장
    private EntityManager entityManager; // 엔티티(테이블과 매핑되는 자바객체)를 관리하는 인스턴스, 엔티티 저장,수정,삭제,조회 와 관련된 일을 진행

    @BeforeAll // 클래스에 존재하는 모든 메소드가 동작하가 전에 최초에 한번만 실행 (테스트 클래스가 동작하기 이전)
    public static void initEntityManagerFactory() { // 스태틱메소드가 필드보다 먼저 생성되기 때문에 static 필드만 사용할 수 있음
        // 엔티티메니저팩토리를 생성해줌 (싱글톤 관리)
        entityManagerFactory = Persistence.createEntityManagerFactory("jpa_test"); // xml과 연결되어있음
    }

    @BeforeEach // 테스트 동작 전에 매번 실행 (테스트 메소드가 동작하기 이전)
    public void initEntityManager() {
        // 엔티티매니저 생성
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Test
    public void 요청1(){
        System.out.println("테스트 메소드1");
        System.out.println("EntityManagerFactory.hashCode() = " + entityManagerFactory.hashCode());
        System.out.println("EntityManager.hashCode() = " + entityManager.hashCode());
    }

    @Test
    public void 요청2(){
        System.out.println("테스트 메소드2");
        System.out.println("EntityManagerFactory.hashCode() = " + entityManagerFactory.hashCode());
        System.out.println("EntityManager.hashCode() = " + entityManager.hashCode());
    }

    @AfterEach // 테스트 동작 후에 매번 실행
    public void destroyEntityManager() {
        //EntityManager 소멸
        entityManager.close();
    }

    @AfterAll
    public static void destroyEntityManagerFactory() {
        // 엔티티매니저팩토리 소멸
        entityManagerFactory.close();
    }
}

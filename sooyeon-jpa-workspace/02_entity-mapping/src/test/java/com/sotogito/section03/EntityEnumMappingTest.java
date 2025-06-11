package com.sotogito.section03;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.Arrays;

public class EntityEnumMappingTest {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager em;

    @BeforeAll
    static void initEntityManagerFactory() {
        entityManagerFactory = Persistence.createEntityManagerFactory("jpa_test");
    }

    @BeforeEach
    void initEntityManager() {
        em = entityManagerFactory.createEntityManager();
    }

    @AfterEach
    void distroyEntityManager() {
        em.close();
    }

    @AfterAll
    static void destroyEntityManagerFactory() {
        entityManagerFactory.close();
    }

    @Test
    void enum_테스트() {
        System.out.println("Userrole.values()" + Arrays.toString(UserRole.values()));
        System.out.println(UserRole.USER);
        System.out.println(UserRole.ADMIN.ordinal());
    }

    @Test
    void enumType_mapping_테스트() {
        User user1 = User.builder()
                .userId("admin01")
                .userPwd("1234")
                .nickname("관리자")
                .phone("010-1234-5678")
                .email("admin01@gmail.com")
                .address("서울")
                .enrollDate(LocalDateTime.now())
                .userRole(UserRole.USER)
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
                .userRole(UserRole.ADMIN)
                .status("Y")
                .build();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        try {
            em.persist(user1);
            em.persist(user2);
            transaction.commit();
        }catch (Exception e) {
            transaction.rollback();
        }
    }
}

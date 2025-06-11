package com.sotogito.section03.bidirection;

import com.sotogito.OrderableStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

/// 많이 안쓰임
public class BiDirectionAssociationTest {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager em;

    @BeforeAll
    static void initEntityManagerFactory() {
        entityManagerFactory = Persistence.createEntityManagerFactory("jpa_test");
    }

    @AfterAll
    static void closeEntityManagerFactory() {
        entityManagerFactory.close();
    }

    @BeforeEach
    void initEntityManager() {
        em = entityManagerFactory.createEntityManager();
    }

    @AfterEach
    void closeEntityManager() {
        em.close();
    }


    @DisplayName("양방향 연관관계 매핑 조회 테스트")
    @Test
    void select_di_direction_test_1() {
        /**
         * 유의사항
         * toString메서드 오버라이딩시 연고나관계로 매핑되어있는 엔티티 활용시 재귀호출이 일어남
         */
        Menu foundedMenu = em.find(Menu.class, 10);
        System.out.println(foundedMenu);

        Category foundedCategory = em.find(Category.class, 10);
        System.out.println(foundedCategory);
        foundedCategory.getMenuList().forEach(System.out::println);
    }

    @Test
    void insert_di_direction_test_1() {
        Category foundedCategory = em.find(Category.class, 4);
        Menu menuToRegister = Menu.builder()
                .menuName("한우국밥")
                .menuPrice(12000)
                .orderableStatus(OrderableStatus.Y)
                .category(foundedCategory)
                .build();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        em.persist(menuToRegister);
        transaction.commit();
    }

    @Test
    void insert_di_direction_test_2() {
        Category categoryToRegister = Category.builder()
                .categoryName("신규카ㅔ")
                .refCategoryCode(3)
                .build();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        em.persist(categoryToRegister);
        transaction.commit();
    }



}

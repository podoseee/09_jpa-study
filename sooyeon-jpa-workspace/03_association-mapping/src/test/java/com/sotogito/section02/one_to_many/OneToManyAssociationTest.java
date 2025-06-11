package com.sotogito.section02.one_to_many;

import com.sotogito.OrderableStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;

public class OneToManyAssociationTest {

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

    /**
     * ### 카테고리 기능
     * - 카테고리 정보 조회(+해당 카테고리의 메뉴들 조회)
     * - 카테고리 등록
     */

    @DisplayName("일대다 연관관계 객체그래프탐색을 이용한 조회 테스트")
    @Test
    void select_one_to_many() {
        int categoryCode = 4;

        Category category = em.find(Category.class, categoryCode);

        System.out.println(category.getCategoryName());
    }

    @DisplayName("일대다 연관관계 객체 삽입 테스트 1")
    @Test
    void insert_one_to_many_1() {
        Category category = Category.builder()
                .categoryName("테스트카테1")
                .refCategoryCode(null)
                .build();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        em.persist(category);
        transaction.commit();
    }

    @DisplayName("일대다 연관관계 객체 삽입 테스트 2")
    @Test
    void insert_one_to_many_2() {
        Menu menu1 = Menu.builder()
                .menuName("테스트 메뉴 2-1")
                .menuPrice(100000)
                .orderableStatus(OrderableStatus.Y)
                .build();

        Menu menu2 = Menu.builder()
                .menuName("테스트 메뉴 2-2")
                .menuPrice(200000)
                .orderableStatus(OrderableStatus.Y)
                .build();

        List<Menu> menuList = List.of(menu1, menu2);

        Category categoryToRegister = Category.builder()
                .categoryName("타테3")
                .refCategoryCode(null)
                .menuList(menuList) /// OneToMany 참조변수
                .build();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(categoryToRegister);
        transaction.commit();
        /**
         * OneToMany의 CascadeType.PERSIST를 설정하지 않으면 영속성 엔티티의 categoryCode상태값만 update된다.
         *
         * 1. INSERT category
         * 2. INSERT menu1, menu2 -> category_code = null
         * 3. UPDATE menu1, menu2 -> SET category_code = 17
         */
    }

}

package com.sotogito.section01.many_to_one;

import com.sotogito.OrderableStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ManyToOneAssociationTest {

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
     * ### 메뉴 기능
     * - 메뉴 정보 조회 (+ 해당 메뉴의 카테고리 상세 정보)
     * - 메뉴 등록      (+ 참조 카테고리가 신규일 경우 카테고리같이 등록)
     */

    @DisplayName("다대일 연관관계 객체그래프탐색을 이용한 조회 테스트") /// 객체그래프탐색 : a.getA().getB();
    @Test
    void select_many_to_one() {
        int menuCode = 5;

        Menu foundedMenu = em.find(Menu.class, menuCode);

        System.out.println(foundedMenu);
        //Menu(menuCode=5, menuName=앙버터김치찜, menuPrice=13000, category=Category(categoryCode=4, categoryName=한식, refCategoryCode=1), orderableStatus=N)

        assertThat(foundedMenu.getCategory().getCategoryCode()).isEqualTo(4);
    }

    @DisplayName("다대일 연관관계 객체지향쿼리를 이용한 조회 테스트")
    @Test
    void JPQL_select_many_to_one() {
        int menuCode = 5;

        /**
         * 1. @Entity의 name 이름을 제시
         * 2. 엔티티의 조인 필드명을 제시
         * 3. 해당 필드명의 참조변수의 엔티티로 넘어가 @Table 확인
         * 4. 조인 확인
         */
        String jpql = "SELECT c.categoryName FROM menu1 m JOIN m.category c WHERE m.menuCode = " + menuCode;
        String foundedCategoryName = em.createQuery(jpql, String.class).getSingleResult();

        System.out.println(foundedCategoryName);
    }

    @DisplayName("다대일 연관관계 객체 삽입 테스트 - 존재하는 category 코드")
    @Test
    void insert_many_to_one_1() {
        Menu menu = Menu.builder()
                .menuName("빙수1")
                .menuPrice(9999)
                .orderableStatus(OrderableStatus.N)
                .category(Category.builder()
                        .categoryCode(4)
                        .build()
                )
                .build();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try{
            em.persist(menu);
            transaction.commit();
        } catch (Exception e){
            transaction.rollback();
            e.printStackTrace();
        }
    }

    @DisplayName("다대일 연관관계 객체 삽입 테스트 - 존재하지 않는 category 코드")
    @Test
    void insert_many_to_one_2() {
//        Category category = Category.builder()
////                .categoryCode(13)
//                .categoryName("요거")
//                .refCategoryCode(3)
//                .build();

        Menu menu = Menu.builder()
                .menuName("요거트sdfsdfs")
                .menuPrice(7777)
                .orderableStatus(OrderableStatus.N)
                .category(Category.builder()
                        .categoryName("요거cccccccccc")
                        .refCategoryCode(2)
                        .build()
                )
                .build();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try{
            em.persist(menu);
            transaction.commit();
        } catch (Exception e){
            transaction.rollback();
            e.printStackTrace();
        }
    }

}

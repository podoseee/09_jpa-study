package com.johnth.section02;

import com.johnth.entity.Menu;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

public class EntityManagerCRUDTest {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @BeforeAll
    public static void initEntityManagerFactory(){
        entityManagerFactory = Persistence.createEntityManagerFactory("jpa_test");
    }

    @BeforeEach
    public void initEntityManager(){
        entityManager = entityManagerFactory.createEntityManager();
    }

    @AfterEach
    public void destroyEntityManager(){
        entityManager.close();
    }

    @AfterAll
    public static void destroyEntityManagerFactory(){
        entityManagerFactory.close();
    }

    @Test
    public void 메뉴코드로_단일_메뉴_조회_테스트(){
        // given
        int menuCode = 2;
        // when
        Menu foundedMenu = entityManager.find(Menu.class, menuCode); // 조회된 Entity가 영속성 컨텍스트에 저장(관리대상)
        // then
        Assertions.assertThat(foundedMenu).isNotNull();
        Assertions.assertThat(foundedMenu.getMenuName()).isEqualTo("우럭스무디");

        System.out.println(foundedMenu);
    }

    @Test
    public void 신규_메뉴_추가_테스트(){
        // given
        // 엔티티 객체 생성
        Menu menu = Menu.builder()
                .menuName("아메리카노")
                .menuPrice(2000)
                .categoryCode(4)
                .orderableStatus("Y")
                .build();

        // when
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        try {
            entityManager.persist(menu); // 영속성 컨텍스트에 엔티티 저장
            entityTransaction.commit();  // 커밋 => 엔티티매니저가 관리하는 모든 엔티티가 실제 db에 반영
        }catch(Exception e){
            e.printStackTrace();
            entityTransaction.rollback(); // 롤백 => 엔티티매니저가 관리하는 모든 엔티티를 이전 상태로 되돌리는 과정
        }

        // then
        Assertions.assertThat( entityManager.contains(menu) ).isTrue();
    }

    @Test
    public void 메뉴명_수정_테스트(){
        // given
        int menuCode = 2;
        String menuNameToChange = "갈치스무디";

        // when
        // 수정하고자 하는 엔티티 조회 => 영속성 컨텍스트에 저장(관리대상)
        Menu menu = entityManager.find(Menu.class, menuCode);

        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        try {
            menu.setMenuName(menuNameToChange);
            entityTransaction.commit();

            /*
                ## Dirty Checking(변경 감지) ##
                영속성 컨텍스트에 저장된 엔티티의 상태가 변경되면
                커밋 시점에서 자동으로 변경내용이 관계형 데이터베이에 반영되게 해줌
             */

        }catch (Exception e){
            e.printStackTrace();
            entityTransaction.rollback();
        }

        // then
        Assertions.assertThat(entityManager.find(Menu.class, menuCode).getMenuName()).isEqualTo(menuNameToChange);

    }

    @Test
    public void 메뉴_삭제_테스트(){
        // given
        int menuCode = 14;

        // when
        // 삭제할 엔티티 조회
        Menu menu = entityManager.find(Menu.class, menuCode);

        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        try {
            entityManager.remove(menu);
            entityTransaction.commit();
        }catch (Exception e){
            e.printStackTrace();
            entityTransaction.rollback();
        }

        // then
        Assertions.assertThat(entityManager.find(Menu.class, menuCode)).isNull();
    }








}

package com.kyungbae.section03;

import com.kyungbae.entity.MenuTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

class EntityLifeCycleTest {

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
    public void 비영속테스트(){

        // 새로 생성한 엔티티 객체 (영속성 컨텍스트에 저장되지 않음)
        // => DB와 관련 없는 비영속 상태
        MenuTest newMenu = MenuTest.builder()
                .menuCode(3)
                .menuName("생갈치쉐이크")
                .menuPrice(6000)
                .categoryCode(10)
                .orderableStatus("Y")
                .build();

        // find() => 조회된 엔티티가 영속 상태 (영속성 컨텍스트에 저장)
        MenuTest foundedMenu = entityManager.find(MenuTest.class, 3);

        System.out.println("newMenu: " + newMenu);
        System.out.println("foundedMenu: " + foundedMenu);
        System.out.println(newMenu == foundedMenu);
    }

    @Test
    public void 영속_find_테스트(){

        int menuCode = 3; // 조회할 메뉴 식별자

        /*
            ## find() 기본 동작 원리
            1. 식별자를 통해 기본적으로 영속성 컨텍스트의 1차 캐시로부터 먼저 조회 (조회시 엔티티 반환)
            2. 조회 실패 시 DB로 부터 Select 쿼리가 실행되면서 조회
            3. 조회된 엔티티를 영속성 컨텍스트에 저장시킴
         */
        // 최초 엔티티 조회 (DB로부터 조회 -> 영속성 컨텍스트에 저장)
        MenuTest foundedMenu1 = entityManager.find(MenuTest.class, menuCode);

        // 엔티티 조회 (영속성 컨텍스트 반환)
        MenuTest foundedMenu2 = entityManager.find(MenuTest.class, menuCode);

        System.out.println("foundedMenu1.hashCode(): " + foundedMenu1.hashCode());
        System.out.println("foundedMenu2.hashCode(): " + foundedMenu2.hashCode());
         // select 쿼리 한번만 실행됨

    }

    @Test
    public void 영속_persist_테스트(){

        MenuTest newMenu = MenuTest.builder()
                .menuCode(30)
                .menuName("수박죽")
                .menuPrice(10000)
                .categoryCode(1)
                .orderableStatus("Y")
                .build();

        entityManager.persist(newMenu);

        MenuTest foundedMenu = entityManager.find(MenuTest.class, 30);

        System.out.println(newMenu == foundedMenu); // true
    }

    @Test
    public void 준영속_detach_테스트(){
        // detach() : 영속 상태의 Entity를 분리해서 보관하는 준영속 상태로 변경
        MenuTest foundedMenu1 = entityManager.find(MenuTest.class, 20); // DB 조회 -> 영속상태
        MenuTest foundedMenu2 = entityManager.find(MenuTest.class, 21); // DB 조회 -> 영속상태

        entityManager.detach(foundedMenu1); // foundedMenu1 => 준영속 상태

        foundedMenu1.setMenuPrice(100); // 준영속 상태의 객체로 변경
        foundedMenu2.setMenuPrice(200); // 영속 상태의 객체로 변경

        Assertions.assertThat(entityManager.find(MenuTest.class, 21).getMenuPrice()).isEqualTo(200);
        Assertions.assertThat(entityManager.find(MenuTest.class, 20).getMenuPrice()).isEqualTo(100);

    }

    @Test
    public void 준영속_clear_테스트(){
        // clear() : 영속성 컨텍스트를 초기화해주는 메소드, 영속상태의 모든 엔티티들이 준영속 상태로 변경

        MenuTest foundedMenu1 = entityManager.find(MenuTest.class, 20); // DB 조회 -> 영속상태
        MenuTest foundedMenu2 = entityManager.find(MenuTest.class, 21); // DB 조회 -> 영속상태

        entityManager.clear();

        foundedMenu1.setMenuPrice(100); // 준영속 상태의 객체로 변경
        foundedMenu2.setMenuPrice(200); // 준영속 상태의 객체로 변경

        Assertions.assertThat(entityManager.find(MenuTest.class, 21).getMenuPrice()).isEqualTo(200);
        Assertions.assertThat(entityManager.find(MenuTest.class, 20).getMenuPrice()).isEqualTo(100);

    }

    @Test
    public void 준영속_close_테스트(){
        // close() : 영속성 컨텍스트를 종료시키는 메소드, EntityManager를 다시 생성해야만 사용할 수 있음

        MenuTest foundedMenu1 = entityManager.find(MenuTest.class, 20); // DB 조회 -> 영속상태
        MenuTest foundedMenu2 = entityManager.find(MenuTest.class, 21); // DB 조회 -> 영속상태

        entityManager.close();

        foundedMenu1.setMenuPrice(100); // 준영속 상태의 객체로 변경
        foundedMenu2.setMenuPrice(200); // 준영속 상태의 객체로 변경

        Assertions.assertThat(entityManager.find(MenuTest.class, 21).getMenuPrice()).isEqualTo(200);
        Assertions.assertThat(entityManager.find(MenuTest.class, 20).getMenuPrice()).isEqualTo(100);

    }

    @Test
    public void 준영속_merge_테스트(){
        // merge() : 준영속 상태의 객체를 다시 영속성 컨텍스트에 추가 후 영속 상태의 객체 반환
        MenuTest foundedMenu = entityManager.find(MenuTest.class, 1); // 영속 상태의 객체
        entityManager.detach(foundedMenu); // 영속 -> 준영속

        MenuTest mergedMenu = entityManager.merge(foundedMenu); // 준영속 -> 영속

        System.out.println("founded: " + foundedMenu.hashCode());
        System.out.println("merged: " + mergedMenu.hashCode());
        System.out.println(foundedMenu == mergedMenu);

        /*
            ## merge() 기본 동작 원리
            1. merge 메소드 호출시 전달된 준영속 엔티티의 식별자를 가지고 1차 캐시로 부터 조회
            2. 1차 캐시에 존재하지 않을 경우 DB로 부터 조회해서 1차 캐시에 저장
            3. 조회된 영속 엔티티 객체와 준영속 엔티티 객체 값을 병합 한 뒤에 반환
                혹은 조회할 수 없는 데이터의 경우 새로 생성해서 병합 ( save or update )
         */
    }

    @Test
    public void 준영속_merge_update_테스트(){

        int menuCode = 1;

        // foundedMenu ( 영속 상태 ) - 영속성 컨텍스트의 1차 캐시에 저장되어있음
        MenuTest foundedMenu = entityManager.find(MenuTest.class, menuCode);

        // foundedMenu ( 준영속 상태 ) - 1차 캐시 제거
        entityManager.detach(foundedMenu);

        // 준영속 상태의 객체 값 변경
        foundedMenu.setMenuName("까나리아메리카노");

        // merge 실행
        MenuTest mergedMenu = entityManager.merge(foundedMenu);
        // 1) foundedMenu의 식별자(1) 를 가지고 1차 캐시로부터 조회 (존재하지 않아 DB로 부터 조회) - select쿼리 실행
        // 2) DB로부터 조회된 Menu 엔티티와 준영속 상태인 foundedMenu 엔티티와 병합되어 새로운 Menu Entity 반환
        // 3) 해당 엔티티는 영속성 컨텍스트에 저장

        System.out.println(mergedMenu);

    }

    @Test
    public void 준영속_merge_save_테스트(){
        int menuCode = 1;
        MenuTest foundedMenu = entityManager.find(MenuTest.class, menuCode);
        entityManager.detach(foundedMenu);

        foundedMenu.setMenuName("시래기라떼");
        foundedMenu.setMenuCode(100);

        // merge 실행
        MenuTest mergeMenu = entityManager.merge(foundedMenu);
        // 1) foundedMenu의 식별자(100)을 가지고 1차 캐시로부터 조회 (존재하지 않음)
        // 2) DB로부터 조회 (존재하지 않음)
        // 3) 새로운 Entity 생성해서 준영속 상태의 객체와 병합하여 반환
        // 4) 해당 Entity가 영속성 컨텍스트에 저장

        System.out.println(mergeMenu);

    }

    @Test
    public void 삭제_remove_테스트(){
        // remove() : 영속 상태의 엔티티를 삭제 상태의 엔티티로 변경
        int menuCode = 1;

        MenuTest foundedMenu = entityManager.find(MenuTest.class, menuCode); // 영속 상태

        entityManager.remove(foundedMenu); // 삭제 상태

        MenuTest removedMenu = entityManager.find(MenuTest.class, menuCode);
        System.out.println(removedMenu); // null
    }
  
}
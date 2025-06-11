package com.jjanggu.section03;

import com.jjanggu.entity.Menu;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

public class EntityLifeCycleTest {

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
    public void 비영속_테스트(){

        // 새로 생성한 에티티 객체 => 영속성 컨텍스트에 저장되지 않음
        // 즉, DB와 관련 없는 비영속 상태
        Menu newMenu = Menu.builder()
                .menuCode(3)
                .menuName("생갈치쉐이크")
                .menuPrice(6000)
                .categoryCode(10)
                .orderableStatus("Y")
                .build();

        // find() => 조회된 엔티티가 영속 상태(영속성 컨텍스트에 저장)
        Menu foundedMenu = entityManager.find(Menu.class, 3);

        System.out.println("newMenu: " + newMenu);
        System.out.println("foundedMenu: " + foundedMenu);
        System.out.println(newMenu == foundedMenu); // false : 주소값이 다르기 때문
    }

    @Test
    public void 영속_find_테스트(){

        int menuCode = 3; // 조회할 메뉴 식별자

        /*
            ## find() 기본 동작 원리 ##
            1. 식별자를 통해 기본적으로 영속성 컨텍스트의 1차 캐시로부터 먼저 조회함
            2. 영속성 컨텍스트로부터 찾지 못하면 DB로 부터 Select 쿼리가 실행되면서 조회
            3. 조회된 엔티티를 영속성 컨텍스트에 저장시킴
         */
        // 최초 엔티티 조회 (DB로 부터 조회)
        Menu foundedMenu1 = entityManager.find(Menu.class, menuCode);

        // 엔티티 조회 (동일 식별자의 엔티티가 영속성 컨텍스트에 존재)
        Menu foundedMenu2 = entityManager.find(Menu.class, menuCode);

        System.out.println("founded1.hashCode: " + foundedMenu1.hashCode());
        System.out.println("founded2.hashCode: " + foundedMenu2.hashCode());

        // select 쿼리 한 번 실행

    }

    @Test
    public void 영속_persist_테스트(){

        // 비영속 상태의 엔티티객체
        Menu newMenu = Menu.builder()
                .menuCode(30)
                .menuName("수박죽")
                .menuPrice(10000)
                .categoryCode(1)
                .orderableStatus("Y")
                .build();

        entityManager.persist(newMenu);

        Menu foundedMenu = entityManager.find(Menu.class, 30);

        System.out.println(newMenu == foundedMenu);

    }

    @Test
    public void 준영속_detach_테스트(){
        // * detach() : 영속 상태의 Entity를 분리해서 보관하는 준영속 상태로 변경
        Menu foundedMenu1 = entityManager.find(Menu.class, 20); // db 조회 => 영속 상태
        Menu foundedMenu2 = entityManager.find(Menu.class, 21); // db 조회 => 영속 상태

        entityManager.detach(foundedMenu1); // foundedMenu1 => 준영속 상태

        foundedMenu1.setMenuPrice(100); // 준영속 상태의 객체로 변경
        foundedMenu2.setMenuPrice(200); // 영속 상태의 객체로 변경

        Assertions.assertThat(entityManager.find(Menu.class, 21).getMenuPrice()).isEqualTo(200);
        Assertions.assertThat(entityManager.find(Menu.class, 20).getMenuPrice()).isEqualTo(100);
    }

    @Test
    public void 준영속_clear_테스트(){
        // * clear( ) : 영속성 컨텍스트를 초기화해주는 메소드, 영속상태의 모든 엔티티들이 준영속 상태로 변경
        Menu foundedMenu1 = entityManager.find(Menu.class, 20); // db 조회 => 영속 상태
        Menu foundedMenu2 = entityManager.find(Menu.class, 21); // db 조회 => 영속 상태

        entityManager.clear();

        foundedMenu1.setMenuPrice(100); // 준영속 상태의 객체로 변경
        foundedMenu2.setMenuPrice(200); // 준영속 상태의 객체로 변경

        Assertions.assertThat(entityManager.find(Menu.class, 21).getMenuPrice()).isEqualTo(200);
        Assertions.assertThat(entityManager.find(Menu.class, 20).getMenuPrice()).isEqualTo(100);
    }

    @Test
    public void 준영속_close_테스트(){
        // * close() : 영속성 컨텍스트를 종료시키는 메소드, EntityManager를 다시 생성해야만 사용할 수 있음
        Menu foundedMenu1 = entityManager.find(Menu.class, 20);
        Menu foundedMenu2 = entityManager.find(Menu.class, 21);

        entityManager.close();

        foundedMenu1.setMenuPrice(100); // 준영속 상태의 객체로 변경
        foundedMenu2.setMenuPrice(200); // 준영속 상태의 객체로 변경

        Assertions.assertThat(entityManager.find(Menu.class, 21).getMenuPrice()).isEqualTo(200);
        Assertions.assertThat(entityManager.find(Menu.class, 20).getMenuPrice()).isEqualTo(100);
    }

    @Test
    public void 준영속_merge_테스트(){
        // * merge() : 준영속 상태의 객체를 더사 영속성 컨텍스트에 추가 후 영속 상
    }

    @Test
    public void 준영속_merge_save_테스트(){
        int menuCode = 1;
        Menu foundedMenu = entityManager.find(Menu.class, menuCode);
        entityManager.detach(foundedMenu);

        foundedMenu.setMenuName("시래기라뗴");
        foundedMenu.setMenuCode(100);

        Menu mergedMenu = entityManager.merge(foundedMenu);
        // 1) foundedMenu의 식별자 (100)를 가지고 1차 캐시로부터 조회 (존재하지 않음)
        // 2) DB로부터 조회 (존재하지 않음)
        // 3) 새로운 Entity 생성해서 준영속 상태의 객체와 병합하여 반환
        // 4) 해당 Entity가 영속성 컨텍스트에 저장

        System.out.println(mergedMenu);
    }

    @Test
    public void 삭제_remove_테스트(){
        // * remove() : 영속 상태의 엔티티를 삭제 상태의 엔티티로 변경
        int menuCode = 1;

        Menu foundedMenu = entityManager.find(Menu.class, menuCode); // 영속 상태

        entityManager.remove(foundedMenu); // 삭제 상태

        Menu removedMenu = entityManager.find(Menu.class, menuCode);
        System.out.println(removedMenu);

        // remove 후에 find는 무조건 null 반환

    }
}

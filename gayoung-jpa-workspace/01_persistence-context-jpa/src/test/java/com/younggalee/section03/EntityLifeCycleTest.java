package com.younggalee.section03;

import com.younggalee.entity.Menu;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import org.assertj.core.api.Assertions;

public class EntityLifeCycleTest {
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


    @Test
    public void 비영속_테스트() { // DB와 아무관계없음
        // 새로 생성한 엔티티 객체 : 영속성 컨텍스트에 저장되지 않음
        // 생성만으로는 영속되지 않음.
        Menu newMenu = Menu.builder()
                .menuCode(3)
                .menuName("생갈치쉐이크")
                .menuPrice(6000)
                .orderableStatus('Y')
                .build(); // 비영속상태


        // 조회된 엔티티가 영속성 컨텍스트에 저장됨
        Menu foundedMenu = entityManager.find(Menu.class, 3);

        // newMenu : 비영속
        // founded : 연속
    }

    @Test
    public void 영속_find_테스트() {
        int menuCode = 3;
        Menu menu = entityManager.find(Menu.class, menuCode); // 최초 엔티티 조회 (DB로 부터 조회)
        // find 동작원리 : 1. 식별자를 통해 기본적으로 영속성 컨텍스트의 1차 캐시로부터 먼저 조회함 (관리대상중에 엔티티를 우선 찾음. 있으면 해당 엔티티 반환. 무조건 select하는게 아님. 미리 select해서 가지고 있는게 있으면 그거 반환함.)
        //               2. 영속성 컨텍스트로부터 찾지 못하면 DB로부터 Select 쿼리가 실행되면서 조회
        //               3. 조회된 엔티티를 영속성 컨텍스트에 저장시킴

        // 엔티티 조회 (동일 식별자의 엔티팉가 영속성 컨텍스트에 존재)
        Menu foundedMenu = entityManager.find(Menu.class, menuCode);

        // find는 2번, 실행되는 select 퀔리는 총 1회
        System.out.println(menu.hashCode());
        System.out.println(foundedMenu.hashCode());
        // 동일한 객체 참조중
    }

    @Test
    public void 영속_persist_테스트(){
        // 비영속 상태의 엔티티객체
        Menu newMenu = Menu.builder()
                .menuCode(30)
                .menuName("수박죽")
                .menuPrice(10000)
                .orderableStatus('Y')
                .build();

        // persist를 통해 영속 상태로 변경
        entityManager.persist(newMenu);

        // 이후, find로 찾으면
        Menu foundedMenu = entityManager.find(Menu.class, 30);

        System.out.println(newMenu == foundedMenu); // 주소값이 동일한지 체크
    }

    @Test
    public void 준영속_detach_테스트(){
        // * detach() : 영속 상태의 entity를 분리해서 보관하는 준영속 상태로 변경
        Menu foundedMenu = entityManager.find(Menu.class, 20);
        Menu foundedMenu2 = entityManager.find(Menu.class, 21); // 둘다 조회 후, 영속상태

        entityManager.detach(foundedMenu); // 준영속상태

        foundedMenu.setMenuPrice(12345); // 20 준영속 1234 변함
        foundedMenu2.setMenuPrice(12345); // 2 - 21 영속 변환 컨텍스트 내에 있음

        Assertions.assertThat(entityManager.find(Menu.class, 21).getMenuPrice()).isEqualTo(12345); // 컨텍스트 내에 존재하는 12345로 변한 객체 반환됨 따라서 True
//        Assertions.assertThat(entityManager.find(Menu.class, 20).getMenuPrice()).isEqualTo(12345); // 컨택스트에 없기 때문에 DB 조회한번더 진행 값은 다름 False
    }


    /// ////////////////////////////////////////////////////

    //@Test
    public void 준영속_clear_테스트(){
        // * clear() : 영속성 컨택스트를 초기화해주는 메소드, 영속상태의 모든 엔티티들이 준영속 상태로 변경
        Menu foundedMenu = entityManager.find(Menu.class, 20);
        Menu foundedMenu2 = entityManager.find(Menu.class, 21); // 둘다 조회 후, 영속상태

        //entityManager.clear(foundedMenu); // 준영속상태

        foundedMenu.setMenuPrice(12345);
        foundedMenu2.setMenuPrice(12345);

        Assertions.assertThat(entityManager.find(Menu.class, 21).getMenuPrice()).isEqualTo(12345);
        Assertions.assertThat(entityManager.find(Menu.class, 20).getMenuPrice()).isEqualTo(12345); // 조회한번더 진행
    }


    //@Test
    public void 준영속_close_테스트(){
        // * clear() : 영속성 컨택스트를 초기화해주는 메소드, 영속상태의 모든 엔티티들이 준영속 상태로 변경
        Menu foundedMenu = entityManager.find(Menu.class, 20);
        Menu foundedMenu2 = entityManager.find(Menu.class, 21); // 둘다 조회 후, 영속상태

       // entityManager.close(foundedMenu); // 준영속상태

        foundedMenu.setMenuPrice(12345);
        foundedMenu2.setMenuPrice(12345);

        Assertions.assertThat(entityManager.find(Menu.class, 21).getMenuPrice()).isEqualTo(12345);
        Assertions.assertThat(entityManager.find(Menu.class, 20).getMenuPrice()).isEqualTo(12345); // 조회한번더 진행
    }

    @Test
    public void 준영속_merge_테스트(){
        // merge() : 준영속 상태의 객체를 다시 영속성 컨텍스트에 추가 후 영속 상태의 객체 반환
        Menu foundedMenu = entityManager.find(Menu.class, 1);
        entityManager.detach(foundedMenu); // 영속 > 준영속

        Menu mergedMenu = entityManager.merge(foundedMenu); // 준영속 > 영속

        System.out.println(foundedMenu == mergedMenu); //false

        /*
            ## merge() 기본 동작 원리 ##
            1. merge 메소드 호출시 전달된 준영속 엔티티의 식별자를 가지고 1차 캐시로 부터 조회
            2. 1차 캐시에 존재하지 않을 경우 DB로 부터 조회해서 1차 캐시에 저장
            3. 조회된 영속 엔티티 객체와 준영속 엔티티 객체 값을 병합한 뒤에 반환
               혹은 조회할 수 없는 데이터의 경우 새로 생성해서 4변합 (save or update)
         */
    }

    @Test
    public void 준영속_merge_update_테스트(){
        int menuCode = 1;

        Menu menu = entityManager.find(Menu.class, menuCode); // 영속성 컨택스트의 1차 캐시에 저장되어있음.

        // foundedMenu (준영속 상태) - 1차 캐시 제거
        entityManager.detach(menu);

        menu.setMenuName("까나리아메리카노"); // 준영속 상태의 객체 값 변경

        Menu mergedMenu = entityManager.merge(menu);
        // 1) menu의 식별자(1)을 가지고 1차 캐시로부터 조회 (존재하지 않아 DB로부터 조회)
        // 2) DB로부터 조회된 Menu 엔티티와 준영속 상태인 foundedMenu 엔티티와 병합되어 새로운 Menu ENtity와 병합되어 새로운 Menu Entity
        // 3) 해당 엔티티는 영속성 컨택스트에 저장
    }

    @Test
    public void 준영속_merge_save_테스트(){
        int menuCode = 1;
        Menu menu = entityManager.find(Menu.class, menuCode);
        entityManager.detach(menu); // 1차 캐시 제거

        menu.setMenuName("시래기라떼");
        menu.setMenuCode(100);

        Menu mergedMenu = entityManager.merge(menu);
        // 1) foundedMenu의 식별자(100)dmf rkwlrh 1차 캐시로부터 조회 (존재하지 않음)
        // 2) DB로부터 조회 (존재하지 않음)
        // 3) 새로운 entity 생성해서 준영속 상태의 객체와 병합하여 반환
        // 4) 해당 entity가 영속성 컨텍스트에 저장

        System.out.println(mergedMenu);
    }

    @Test
    public void 삭제_remove_테스트() {
        // * remove() : 영속 상태의 엔티티를 삭제 상태의 엔디디로 변경
        int menuCode = 1;

        Menu foundedMenu = entityManager.find(Menu.class, menuCode);

        entityManager.remove(foundedMenu); // 삭제 상태

        Menu removedMenu = entityManager.find(Menu.class, menuCode);
        System.out.println(removedMenu);

        // remove 후에 find는 무조건 null 반환. 다시 조회되지 않음. 삭제상태일 뿐 관리대상에 존재
    }


}

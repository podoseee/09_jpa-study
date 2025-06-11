package com.sotogito.section03;

import com.sotogito.entity.Menu;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

public class EntityLifeCycle {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @BeforeAll
    static void initEntityManagerFactory() {
        entityManagerFactory = Persistence.createEntityManagerFactory("jpa_test");
    }

    @BeforeEach
    void initEntityManager() {
        entityManager = entityManagerFactory.createEntityManager();
    }

    @AfterEach
    void distroyEntityManager() {
        entityManager.close();
    }

    @AfterAll
    static void destroyEntityManagerFactory() {
        entityManagerFactory.close();
    }

    @Test
    void 비영속_테스트() {
        /**
         * 새로 생성한 엔티티 객체 -> 영속성 컨텍스트에 저장되지 않음
         * 즉, DB와 관련 없는 비영속 상태
         */
        Menu menu = Menu.builder() // 비영속 상태
                .menuCode(3)
                .menuName("생갈치쉐이크")
                .menuPrice(6000)
                .categoryCode(10)
                .orderableStatus("Y")
                .build();

        Menu foundedMenu = entityManager.find(Menu.class, menu.getMenuCode()); //영속 상태

        Assertions.assertNotSame(foundedMenu, menu);  /// 비영속과 영속상태는 동일성(==)을 보장하지 않음 - 주소값 다름
    }

    @Test
    void 영속_find_테스트() {
        int menuCode = 3;

        /**
         * find() 기본 동작 원리
         * 1. 식별자를 통해 기본적으로 영속성 컨텍스트의 1차 캐시로부터 먼저 조회함
         * 2. 영속성 컨텍스트로부터 차지 못하면 DB로부터 SELECT 쿼리가 실행되면서 조회
         * 3. 조회된 엔티티를 영속성 컨텍스트에 저장시킴
         */
        Menu menu1 = entityManager.find(Menu.class, menuCode); /// 캐시 확인 -> 없음 -> DB 접근 -> 1차 캐시 저장
        Menu menu2 = entityManager.find(Menu.class, menuCode); /// 캐시 확인 -> 있음 -> 캐시에서 가져옴(DB접근X)
        // menu1, menu2는 동일한(==) 객체이기 때문에 각각 다른 set은 불가함 - 마지막 set 반영

        Assertions.assertEquals(menu1, menu2); /// 동일성이 보장됨
    }

    @Test
    void 영속_persist_테스트() {
        Menu newMenu = Menu.builder() // 비영속 상태
                .menuName("수박죽")
                .menuPrice(10000)
                .categoryCode(1)
                .orderableStatus("Y")
                .build();

        entityManager.persist(newMenu);

        Menu foundedMenu = entityManager.find(Menu.class, newMenu.getMenuCode()); //persist 직후 DB에 INSERT 쿼리를 날리지 않았더라도 캐시에서 찾을 수 있음

        System.out.println(newMenu == foundedMenu); //true
    }

    @Test
    void 준영속_detach_테스트() {
        Menu foundedMenu1 = entityManager.find(Menu.class, 20); //db조회 -> 영속
        Menu foundedMenu2 = entityManager.find(Menu.class, 21); //db조회 -> 영속

        entityManager.detach(foundedMenu1); //영속 -> 준영속

        foundedMenu1.setMenuPrice(123);
        foundedMenu2.setMenuPrice(123);

        Assertions.assertNotEquals(123, entityManager.find(Menu.class,20).getMenuPrice()); ///db에 반영되지 않음
        Assertions.assertEquals(123, entityManager.find(Menu.class,21).getMenuPrice());
    }

    @Test
    void 준영속_clear_테스트() {
        Menu foundedMenu1 = entityManager.find(Menu.class, 20);
        Menu foundedMenu2 = entityManager.find(Menu.class, 21);

        entityManager.clear(); ///영속성 컨텍스트 초기화 (캐시 비우기)

        foundedMenu1.setMenuPrice(123);
        foundedMenu2.setMenuPrice(123);

        Assertions.assertNotEquals(123, entityManager.find(Menu.class,20).getMenuPrice());
        Assertions.assertNotEquals(123, entityManager.find(Menu.class,21).getMenuPrice());
    }

    @Test
    void 준영속_close_테스트() {
        Menu foundedMenu1 = entityManager.find(Menu.class, 20);
        Menu foundedMenu2 = entityManager.find(Menu.class, 21);

        entityManager.close(); ///EntityManager 자체를 종료 -> 아예 작업 불가

        foundedMenu1.setMenuPrice(123);
        foundedMenu2.setMenuPrice(123);

        //java.lang.IllegalStateException: Session/EntityManager is closed
//        Assertions.assertEquals(123, entityManager.find(Menu.class,20).getMenuPrice());
//        Assertions.assertEquals(123, entityManager.find(Menu.class,21).getMenuPrice());
    }

    @Test
    void 준영속_merge_테스트() {
        /// 준영속 상태의 객체를 다시 영속석 컨텍스트에 추가 후 영속상태의 갹체 반환

        Menu foundedMenu = entityManager.find(Menu.class, 1); //영속
        entityManager.detach(foundedMenu); //영속 -> 준영속

        Menu mergedMenu = entityManager.merge(foundedMenu); ///준영속 -> 영속
        /**
         * merge()의 기본 동작 원리
         * 1. merge 메서드 호출시 전달된 준영속 엔티티의 식별자를 가지고 1차 캐시로 부터 조회
         * 2. 1차 캐시에 존재하지 않을 경우 DB로부터 조회해서 1차 캐시에 저장
         * 3. 조회된 영속 엔티티 객체와 중영속 엔티티 객체 값을 병합한 뒤 반환
         *      -> 만약 DB에도 존재하지 않을 경우 새로운 Entity 생성
         *
         *
         * marge처리하면 영속처리에 사용한 비영속 객체는 여전히  비영속객체로 남음 - 동기화 안됨
         */
        //mergedMenu : 새로운 영속 상태의 복제 객체가 만들어지고 반환됨
        //foundedMenu : 여전히 준영속

        assertThat(foundedMenu == mergedMenu).isFalse();
    }

    @Test
    void 준영속_merge_update_테스트() {
        int menuCode = 1;

        Menu foundedMenu = entityManager.find(Menu.class, menuCode);

        entityManager.detach(foundedMenu); ///영속 -> 준영속 : 1차 캐시에서 제거
        foundedMenu.setMenuName("까나리 아메리카노");

        Menu mergedMenu = entityManager.merge(foundedMenu); ///조회시 캐시에 없어서 db에서 조회
        /**
         * foundedMenu가 준영속 상태에서 상태가 변경이 되고, merge시 db에 접근하여 foundedMenu의 set전의 name이 출려될 거같지만
         * 준영속 상태의 객체를 1차 캐시에서(영속상태에서) 변경하지 않았더라도 mergedMenu에는 변경된 상태가 반영이된다.
         *
         * 1. 준영속 @ID 식별자를 확인
         * 2. 그 ID로 영속성 컨텍스트(1차 캐시 or DB)에서 영속 객체를 찾음
         * 3. 그 영속 객체에 준영속의 모든 필드 값을 복사(set)함 :B.setX(A.getX()), B.setY(A.getY()) -> id만 그대로 사용함
         *          -> 만약 db에 접근했을 때 준영속 엔티티의 id가 없을 경우 새로운 엔티티를 생성하고(insert) merge
         * 4. 복사된 그 영속 객체를 반환함 → mergedMenu
         *
         * 복사된 그 영속 객체를 반환함 → mergedMenu
         */
        assertThat(mergedMenu.getMenuName()).isEqualTo("까나리 아메리카노");
    }

    @Test
    void 준영속_merge_save_테스트() {
        int menuCode = 1;
        Menu foundedMenu = entityManager.find(Menu.class, menuCode);
        entityManager.detach(foundedMenu);

        foundedMenu.setMenuName("시래기라떼");
        foundedMenu.setMenuCode(100);

        Menu mergedMenu = entityManager.merge(foundedMenu); //@id 100을 db에서 조회하지만 없어서 새로운 entity를 생성하고 merge

        assertThat(mergedMenu.getMenuCode()).isEqualTo(100);
        assertThat(mergedMenu.getMenuName()).isEqualTo("시래기라떼");
    }

    @Test
    void 삭제_remove_테스트() {
        ///  remove : 영속 상태의 엔티티를 삭제 상태의 엔티티로 변경
        int menuCode = 1;

        Menu foundedMenu = entityManager.find(Menu.class, menuCode);

        entityManager.remove(foundedMenu); /// 캐시 + db에서 삭제

        Menu removedMenu = entityManager.find(Menu.class, menuCode);

        assertThat(removedMenu).isNull();
    }



}

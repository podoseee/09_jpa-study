package com.sotogito.section02;

import com.sotogito.entity.Menu;
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

    @DisplayName("메뉴코드로 단일 메뉴 조회 테스트")
    @Test
    void select_menu() {
        int menuCode = 2;

        Menu foundedMenu = entityManager.find(Menu.class, menuCode); //조회된 Entity가 영속성 컨텍스트에 저장(관리대상)

        Assertions.assertThat(foundedMenu).isNotNull();
        Assertions.assertThat(foundedMenu.getMenuName()).isEqualTo("우럭스무디");
    }

    @DisplayName("신규 메뉴 추가 테스트")
    @Test
    void insert_menu() {
        Menu newMenu = Menu.builder()
                .menuName("아아")
                .menuPrice(2000)
                .categoryCode(4)
                .orderableStatus("Y")
                .build();

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
//        entityManager.flush(); db에 명시적으로 commit전에 일단 담음 -> commit전이라 rollback가능
        try {
            entityManager.persist(newMenu); ///1차 캐시에 담음

            transaction.commit(); /// db에 담음
        } catch (Exception e) {
            transaction.rollback();
        }

        Assertions.assertThat(entityManager.contains(newMenu)).isTrue();
    }

    @DisplayName("메뉴명 수정 테스트")
    @Test
    void update_menu() {
        int menuCode = 2;
        String menuNameToChange = "갈치스무디";

        /**
         * 수정하고자 하는 엔티티 조회 => 영속성 컨테스트에 저장(관리대상)
         * 영속성 컨텍스트(JPA 1차 캐시)에서 DB 데이터를 다루기 위해서는 find() 또는 쿼리(JPQL 등)를 통해 먼저 객체를 가져와야 함.
         */
        Menu menu = entityManager.find(Menu.class, menuCode);
        /// 내부적으로 스냅샷 생성: {menuName: "라면", menuPrice: 9000},  복제해서 보관 => 이 복제품으로 변경 감지함
        /**
         * 변경할 원본 entity : EntityManager 내부 PersistenceContext에 저장 -> 1차 캐시
         * 복사본 : EntityEntry라는 객체 안에 저장, Object[] loadedState=  배열 형태로 엔티티의 초기 상태 저장
         *
         * EntityManager의 PersistanceContext는
         * PersistenceContext
         * ├── Entity instance (Java 객체)
         * └── EntityEntry ← 바로 이게 메타데이터
         *     ├── status            → 현재 상태 (영속, 삭제, 준영속 등)
         *     ├── loadedState       → 스냅샷 (처음 로딩된 값 복사본)
         *     ├── entityName        → 엔티티 이름
         *     ├── id                → 기본 키
         *     └── version           → 버전 값 (낙관적 락킹용)
         */

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {
            menu.setMenuName(menuNameToChange); /// 1차 캐시에서 변경
            transaction.commit(); /// db 변경,
            /// 만약 set에서 변경 사항이 없다면 DB에 UPDATE 쿼리 전혀 발생하지 않음 - 쓰기지연

            /**
             * ## Dirty Checking (변경 감지)
             * 영속성 컨텍스트에 저장된 엔티티의 상태가 변경되면
             * 커밋 시점에서 자동으로 변경 내용이 관계형 db에 반영되게 해줌
             */

        } catch (Exception e) {
            transaction.rollback();
        }

        Assertions.assertThat(entityManager.find(Menu.class, menuCode).getMenuName()).isEqualTo(menuNameToChange); ///entityManager가 close될떄까지 캐시에 있음
    }

    @DisplayName("메뉴 삭제 테스트")
    @Test
    void delete_menu() {
        int menuCode = 14;

        Menu menu = entityManager.find(Menu.class, menuCode); ///1차 캐시에 저장 + 영속 상태 진입

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {
            entityManager.remove(menu); /// 해당 엔티티를 삭제 예약 상태로 표시, 영속성 컨텍스트에서 해당 엔티티를 삭제 대기 큐에 등록
            /// 여기까지는 DB에 DELETE 쿼리가 전혀 실행되지 않음 -> 쓰기 지연
            transaction.commit(); ///내부적으로 flush()가 자동 호출, DB삭제
        } catch (Exception e) {
            transaction.rollback();
        }

        Assertions.assertThat(entityManager.contains(menu)).isFalse();
    }

}

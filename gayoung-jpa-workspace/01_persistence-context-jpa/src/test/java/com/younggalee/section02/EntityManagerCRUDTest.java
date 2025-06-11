package com.younggalee.section02;

import com.younggalee.entity.Menu;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;


public class EntityManagerCRUDTest {
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

    @Test // select : find
    public void 메뉴코드로_단일_메뉴_조회_테스트() {
        // given
        int menuCode = 2;
        // when
        Menu foundedMenu = entityManager.find(Menu.class, menuCode); // 엔티티 클래스, 조회할 식별자 // 클래스에 지정한 엔티티 타입으로 반환됨.
        //조회된 엔티티가 영속성 컨텍스트에 저장되고, 관리대상으로 삼음
        // then
        Assertions.assertThat(foundedMenu).isNotNull(); //비교대상과 동일하다면 Test 통과
        Assertions.assertThat(foundedMenu.getMenuName()).isEqualTo("우럭스무디");
    }

    @Test // insert : persist, commit
    public void 신규_메뉴_추가_테스트(){
        // given
        // 엔티티 객체 생성
        Menu menu = Menu.builder()
                .menuName("아메리카노")
                .menuPrice(2000)
                .categoryCode(4)
                .orderableStatus('Y')
                .build();

        // 트랜젝션 객체 생성
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin(); // 트랜젝션 시작 제시

        try {
            entityManager.persist(menu);// 영속성 컨텍스트에 엔티티객체 저장
            transaction.commit(); // 커밋 : 엔티티 메니저가 관리하는 관리대상의 모든 엔티티가 DB에 반영되도록
        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback(); // 롤백 : 엔티티매니저가 관리하는 모든 엔티티를 이전 상태로 되돌리는 과정
        }

        // then
        Assertions.assertThat(entityManager.contains(menu)).isTrue(); // menu라는 엔티티 객체가 영속성컨테이너에 포함되어 있는지 (true면 테스트성공)
    }

    @Test  // update : set , commit
    public void 메뉴명_수정_테스트(){
        // givne
        int menuCode = 2;
        String menuNameToChange = "갈치스무디";

        // when
        // 수정하고자 하는 엔티티 조회 >> 영속성 컨텍스트에 저장(관리대상)
        Menu menu = entityManager.find(Menu.class, menuCode);

        EntityTransaction transaction = entityManager.getTransaction(); //스냅샷 1차 캐시에 저장됨
        // jpa는 엔티티객체를 처음 불러올때의 상태(스냅샷)을 내부에 저장한다고 함.
        transaction.begin();

        try {
            menu.setMenuName(menuNameToChange); //스냅샷과 비교하여 변경된 사항이 감지되면 update sql을 작성하여 쓰기지연 sql저장소에 저장함
            transaction.commit(); // update 진행됨

            /*
            Dirty Checking (변경 감지)

            영속성 컨텍스트에 저장된 엔티티의 상태가 변경되면
            커밋 시점에서 자동으로 변경내용이 관계형 데이터베이스에 반영되게 해줌
             */
        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
        }
        Assertions.assertThat(entityManager.find(Menu.class, menuCode).getMenuName()).isEqualTo(menuNameToChange);
    }

    @Test // delete : remove, commit
    public void 메뉴_삭제_테스트(){
        // given
        int menuCode = 14;

        // when
        // 삭제할 엔티티 조회
        Menu menu = entityManager.find(Menu.class, menuCode);

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {
            entityManager.remove(menu);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
        }

        // then
        Assertions.assertThat(entityManager.find(Menu.class, menuCode)).isNull();
    }

}

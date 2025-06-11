package com.sotogito.section04.practice;

import com.sotogito.section04.practice.entity.Locker;
import com.sotogito.section04.practice.entity.Member;
import com.sotogito.section04.practice.entity.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.concurrent.locks.Lock;

/**
 * 예제
 * 한 팀에서는 여러 회원들이 존재하며 각 회원들은 각각 하나의 라커를 가지고 있다.
 *
 */

public class PracticeTest {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager em;

    @BeforeAll
    static void initEntityManagerFactory() {
        entityManagerFactory = Persistence.createEntityManagerFactory("practice-test");
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

    @DisplayName("db생성용")
    @Test
    void test() {
    }

    @Test
    void insert_locker() {
        Locker locker = Locker.builder()
                .lockerName("첫랏커")
                .build();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            em.persist(locker);
            transaction.commit();
        }catch (Exception e) {
            transaction.rollback();
        }
    }

    @Test
    void insert_team() {
        Team team = Team.builder()
                .teamName("팀1")
                .build();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            em.persist(team);
            transaction.commit();
        }catch (Exception e) {
            transaction.rollback();
        }
    }

    @Test
    void insert_member() {
        Locker foundedLocker = em.find(Locker.class, 2);
        Team foundedTeam = em.find(Team.class, 2);

        Member member = Member.builder()
                .memberName("이강인1")
                .team(foundedTeam)
                .locker(foundedLocker)
                .build();

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            em.persist(member);
            transaction.commit();
        }catch (Exception e) {
            transaction.rollback();
        }
    }


}

package com.podoseee.springdatajpa.menu.repository;

import com.podoseee.springdatajpa.menu.entity.Menu;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.lang.management.MemoryUsage;
import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Integer> {

    //@Query(value="SELECT menu_code, menu_name, menu_price, category_code, orderable_status FROM tbl_menu WHERE menu_price = ?1", nativeQuery = true)
    //@Query(value="SELECT menu_code, menu_name, menu_price, category_code, orderable_status FROM tbl_menu WHERE menu_price = :test", nativeQuery = true)
    //List<Menu> findByMenuPrice(@Param("test") int price);

    List<Menu> findByMenuPriceEquals(int price); // WHERE m.menuPrice = xxx
    List<Menu> findByMenuPriceGreaterThanEqual(int price); // WHERE m.menuPrice >= xxx
    List<Menu> findByMenuPriceGreaterThanEqual(int price, Sort sort); // WHERE m.menuPrice >= xxx ORDER BY
    List<Menu> findByMenuPriceGreaterThanEqualOrderByMenuPriceDesc(int price);

    // 위의 Native Query 를 쿼리 메소드로 대체
    List<Menu> findByMenuNameContaining(String name); // WHERE m.menuName LIKE '%xx%'

    List<Menu> findByMenuPriceGreaterThanEqualAndMenuNameContaining(int price, String name); // WHERE m.menuPrice >= xxx AND m.menuName LIKE '%xx%'
}
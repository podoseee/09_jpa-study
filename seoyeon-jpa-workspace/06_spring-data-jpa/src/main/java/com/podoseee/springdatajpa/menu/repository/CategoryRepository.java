package com.podoseee.springdatajpa.menu.repository;

import com.podoseee.springdatajpa.menu.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query(value="SELECT category_code, category_name, ref_category_code FROM tbl_category WHERE ref_category_code IS NOT NULL"
            , nativeQuery = true)
    List<Category> findAllSubCategory();

    List<Category> findByRefCategoryCodeIsNotNullOrderByCategoryCodeDesc();
}

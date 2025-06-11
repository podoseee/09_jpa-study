package com.sotogito.section02.one_to_many;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter

@Entity(name = "category2")
@Table(name = "tbl_category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_code", nullable = false)
    private int categoryCode;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(name = "ref_category_code")
    private Integer refCategoryCode;

    @OneToMany(
            fetch = FetchType.LAZY, //(기본값)
            cascade = CascadeType.PERSIST
    )
    @JoinColumn(name = "category_code")
    private List<Menu> menuList; ///DB 테이블에 "직접 저장되진 않지만", 연관관계 매핑을 위해 반드시 필요한 필드

}

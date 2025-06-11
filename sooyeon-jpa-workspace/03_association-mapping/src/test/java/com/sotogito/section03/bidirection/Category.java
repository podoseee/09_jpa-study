package com.sotogito.section03.bidirection;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter

@Entity(name = "category3")
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

    @OneToMany(mappedBy = "category") /// 주인Entity의 join 할 필드명
    private List<Menu> menuList;


    @Override
    public String toString() {
        return "Category [categoryCode=" + categoryCode +
                ", categoryName=" + categoryName +
                ", refCategoryCode=" + refCategoryCode +
                ", menuList=" + menuList +
                "]";
    }

}

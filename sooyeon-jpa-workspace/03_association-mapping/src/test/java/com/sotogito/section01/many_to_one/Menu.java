package com.sotogito.section01.many_to_one;

import com.sotogito.OrderableStatus;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString

@Entity(name = "menu1")
@Table(name = "tbl_menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_code", nullable = false)
    private int menuCode;

    @Column(name = "menu_name", nullable = false)
    private String menuName;

    @Column(name = "menu_price", nullable = false)
    private int menuPrice;

//    @Column(name = "category_code")
//    private Integer categoryCode;

    @ManyToOne(
            fetch = FetchType.EAGER, //즉시로딩(기본값) - JOIN으로 일단 가져옴
//            fetch = FetchType.LAZY ///지연로딩 - find() 호출에서 menu먼저 찾고, 만약 추후 cateogry의 데이터가 요구될때 tbl_category 조회
            cascade = CascadeType.PERSIST
    )
    @JoinColumn(name = "category_code") //tbl_menu 의 cateogry_code컬럼으로 조인하겠다.
    private Category category;
    /**
     *     left join
     *         tbl_category c1_0
     *             on c1_0.category_code=m1_0.category_code
     */

    @Enumerated(EnumType.STRING)
    @Column(name = "orderable_status", nullable = false)
    private OrderableStatus orderableStatus;


}

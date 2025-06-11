package com.sotogito.section02.one_to_many;

import com.sotogito.OrderableStatus;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString

@Entity(name = "menu2")
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

    @Column(name = "category_code")
    private Integer categoryCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "orderable_status", nullable = false)
    private OrderableStatus orderableStatus;

}

package com.sotogito.section03.bidirection;

import com.sotogito.OrderableStatus;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder

@Entity(name = "menu3")
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

    @ManyToOne
    @JoinColumn(name = "category_code") /// FK를 가지고있는 Entity가 주인
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "orderable_status", nullable = false)
    private OrderableStatus orderableStatus;

    @Override
    public String toString() {
        return "Menu [menuCode=" + menuCode +
                ", menuName=" + menuName +
                ", menuPrice=" + menuPrice +
                ", orderableStatus=" + orderableStatus +
                "]";
    }

}

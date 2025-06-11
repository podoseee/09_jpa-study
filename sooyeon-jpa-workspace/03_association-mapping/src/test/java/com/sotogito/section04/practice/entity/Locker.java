package com.sotogito.section04.practice.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString

@Entity(name = "locker")
@Table(name = "tbl_locker")
public class Locker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "locker_id", nullable = false)
    private int lockerId;

    @Column(name = "locker_name", nullable = false)
    private String lockerName;

}

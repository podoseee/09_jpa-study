package com.sotogito.section04.practice.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString

@Entity(name = "member")
@Table(name = "tbl_member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private int memberId;

    @Column(name = "member_name", nullable = false)
    private String memberName;

    @ManyToOne(
            fetch = FetchType.EAGER,
            cascade = CascadeType.PERSIST
    )
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST
    )
    @JoinColumn(name = "locker_id")
    private Locker locker;


    public void changeTeam(Team team) {
        this.team = team;
    }

    public void changeLocker(Locker locker) {
        this.locker = locker;
    }

}

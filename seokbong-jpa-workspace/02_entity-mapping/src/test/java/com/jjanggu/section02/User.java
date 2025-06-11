package com.jjanggu.section02;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

/*
    ## 컬럼 매핑시 @Column에 사용 가능한 속성들 ##
    1. name             : 매핑할 테이블의 컬럼명
    2. insertable       : 엔티티 저장시 필드 저장 여부 (default:true)
    3. updatable        : 엔티티 수정시 필드 수정 여부 (default:true)
    4. table            : 엔티티와 매핑될 테이블 이름, 하나의 엔티티를 두개 이상의 테이블에 매핑할 때 사용(@SecondaryTable사용)
    5. nullable         : null 허용 여부 설정, not null 제약조건에 해당함 (default:true)
    6. unique           : 컬럼의 유일성 제약조건에 해당함 (default:false), 두 개 이상의 컬럼을 묶어 부여하고자 할 경우 클래스 레벨에서 uniqueConstraints 속성을 활용
    7. columnDefinition : 직접 컬럼의 DDL을 지정
    8. length           : 문자열 길이, String 타입에서만 사용 (default:255)
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString

@Entity
@Table(
        name = "tbl_user",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"phone", "enroll_date"})}
)
public class User {
    @Id
    @Column(name = "user_no")
    private int userNo;
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Column(name = "user_pwd", nullable = false)
    private String userPwd;;
    @Column(name = "nickname", nullable = false)
    private String nickname;
    @Column(name = "phone", columnDefinition = "varchar(13) default '010-0000-0000'")
    private String phone;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "address")
    private String address;

    @Column(name = "enroll_date")
    //@Temporal(TemporalType.TIMESTAMP) // datetime, 생략시 기본값
    //@Temporal(TemporalType.DATE)      // date
    //@Temporal(TemporalType.TIME)      // time
    //private Date enrollDate;

    private LocalDateTime enrollDate;   // datetime
    // private LocalDate enrollDate;    // date
    // private LocalTime enrollDate;    // time

    @Column(name = "user_role")
    private String userRole;
    @Column(name = "status", length = 1)
    private String status;
}

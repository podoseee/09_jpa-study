package com.kyungbae.section01;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class test1 {

    @Id
    @Column
    private int testNo;
    @Column
    private String testName;
    @Column
    private Date testDate;

}

/*
    drop table if exists test1

    create table test1 (
        testNo integer not null,
        testDate datetime(6),
        testName varchar(255),
        primary key (testNo)
    ) engine=InnoDB
 */

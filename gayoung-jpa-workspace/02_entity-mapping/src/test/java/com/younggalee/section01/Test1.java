package com.younggalee.section01;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class Test1 {
    @Id
    @Column
    private int testNo;

    @Column
    private String testName;

    @Column
    private Date testDate;
}

/*
    drop table if exists Test1;
    create table Test1 (
        testNo integer not null,
        testDate datetime(6),
        testName varchar(255),
        primary key (testNo)
    ) engine=InnoDB
 */
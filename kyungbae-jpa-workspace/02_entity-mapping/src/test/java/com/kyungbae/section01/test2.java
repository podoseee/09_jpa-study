package com.kyungbae.section01;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Date;

@Entity(name="test")
public class test2 {

    @Id
    @Column(name = "no")
    private int testNo;
    @Column(name = "name")
    private String testName;
    @Column(name = "date")
    private Date testDate;

}
/*
    drop table if exists test

    create table test (
        no integer not null,
        date datetime(6),
        name varchar(255),
        primary key (no)
    ) engine=InnoDB
 */
package com.kyungbae.section01;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;

@Entity(name="entity_test")
@Table(name="test_custom_tbl")
public class test3 {

    @Id
    @Column(name = "no")
    private int testNo;
    @Column(name = "name")
    private String testName;
    @Column(name = "date")
    private Date testDate;

}
/*
    drop table if exists test_custom_tbl

    create table test_custom_tbl (
        no integer not null,
        date datetime(6),
        name varchar(255),
        primary key (no)
    ) engine=InnoDB
 */
package com.jjanggu.section01;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;

@Entity(name = "entity_test")
@Table(name = "test_custom_tbl")
public class Test3 {
    @Id
    @Column(name = "test_no")
    private int testNo;
    @Column(name = "test_name")
    private String testName;
    @Column(name = "test_date")
    private Date testDate;
}

/*
    drop table if exists test_custom_tbl
    create table test_custom_tbl (
        test_no integer not null,
        test_date datetime(6),
        test_name varchar(255),
        primary key (test_no)
    ) engine=InnoDB
 */

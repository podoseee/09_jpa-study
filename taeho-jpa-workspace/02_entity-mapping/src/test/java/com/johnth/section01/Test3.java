package com.johnth.section01;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Date;

@Entity(name="test_custom_tbl")
public class Test3 {

    @Id
    @Column(name="test_no")
    private int testNo;

    @Column(name="test_name")
    private String testName;

    @Column(name="test_date")
    private Date testDate;

}

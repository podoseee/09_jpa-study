package com.sotogito.section01;

import jakarta.persistence.*;

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

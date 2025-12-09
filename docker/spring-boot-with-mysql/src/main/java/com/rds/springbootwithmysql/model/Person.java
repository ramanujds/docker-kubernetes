package com.rds.springbootwithmysql.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Person {

    @Id
    private Long id;
    private String name;
    private int age;

}

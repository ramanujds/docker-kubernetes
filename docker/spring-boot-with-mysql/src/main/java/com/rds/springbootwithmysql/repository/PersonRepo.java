package com.rds.springbootwithmysql.repository;

import com.rds.springbootwithmysql.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepo extends JpaRepository<Person,Long> {
}

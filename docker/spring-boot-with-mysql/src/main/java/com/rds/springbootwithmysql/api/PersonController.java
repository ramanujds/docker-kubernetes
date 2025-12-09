package com.rds.springbootwithmysql.api;

import com.rds.springbootwithmysql.model.Person;
import com.rds.springbootwithmysql.repository.PersonRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/persons")
public class PersonController {


    private final PersonRepo personRepo;

    public PersonController(PersonRepo personRepo) {
        this.personRepo = personRepo;
    }

    @PostMapping
    public Person addPerson(@RequestBody Person person) {
        return personRepo.save(person);
    }

    @GetMapping
    public List<Person> getAllPersons() {
        return personRepo.findAll();
    }

}

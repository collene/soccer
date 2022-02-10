package ca.collene.soccer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.collene.soccer.entities.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Person findByName(String name);
}

package ca.collene.soccer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import ca.collene.soccer.entities.Person;
import ca.collene.soccer.repositories.PersonRepository;

@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    public Person createPerson(String name) throws NameAlreadyExistsException {
        Person newPerson = new Person(name);
        try {
            return personRepository.save(newPerson);
        } catch(DataIntegrityViolationException e) {
            throw new NameAlreadyExistsException("A person with name " + name + " already exists");
        }
    }

    public Person getPerson(String name) throws PersonDoesNotExistException {
        Person person = personRepository.findByName(name);
        if(person == null) {
            throw new PersonDoesNotExistException("Person with name " + name + " was not found");
        }
        return person;
    }
}

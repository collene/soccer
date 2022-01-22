package ca.collene.soccer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import ca.collene.soccer.entities.Person;
import ca.collene.soccer.repositories.PersonRepository;
import ca.collene.soccer.services.NameAlreadyExistsException;
import ca.collene.soccer.services.PersonDoesNotExistException;
import ca.collene.soccer.services.PersonService;

@SpringBootTest(properties = {
    InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
    ScriptShellApplicationRunner.SPRING_SHELL_SCRIPT_ENABLED + "=false"
})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class PersonServiceTests {
    @Autowired
    private PersonService personService;

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void create_person_works() throws Exception {
        final String testPersonName = "Test person";
        assertThat(personRepository.count(), is(equalTo(0l)));        
        assertThrows(PersonDoesNotExistException.class, () -> {
            personService.getPerson(testPersonName);
        });
        Person newPerson = personService.createPerson(testPersonName);
        assertThat(personService.getPerson(testPersonName), is(equalTo(newPerson)));
        assertThat(personRepository.count(), is(equalTo(1l)));
    }

    @Test
    public void create_person_with_duplicate_name_fails() throws Exception {
        final String samePersonName = "Test person";
        personService.createPerson(samePersonName);        
        assertThat(personRepository.count(), is(equalTo(1l)));

        // make sure the exception is thrown
        assertThrows(NameAlreadyExistsException.class, () -> {
            personService.createPerson(samePersonName);
        });

        // make sure that a second person hasn't been added after the exception was thrown
        assertThat(personRepository.count(), is(equalTo(1l)));
    }

    @Test
    public void get_person_by_name_works() throws Exception {    
        final String testPersonName = "Test person";
        Person newPerson = personService.createPerson(testPersonName);            
        assertThat(personService.getPerson(testPersonName), is(equalTo(newPerson)));
    }

    @Test
    public void get_person_by_name_that_does_not_exist_fails() {
        final String testPersonName = "Person that does not exist";
        assertThrows(PersonDoesNotExistException.class, () -> {
            personService.getPerson(testPersonName);
        });
    }
}

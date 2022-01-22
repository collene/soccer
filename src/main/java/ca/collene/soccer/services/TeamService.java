package ca.collene.soccer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import ca.collene.soccer.entities.Person;
import ca.collene.soccer.entities.Team;
import ca.collene.soccer.repositories.TeamRepository;

@Service
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PersonService personService;

    public Team createTeam(String name) throws NameAlreadyExistsException {
        Team newTeam = new Team(name);
        try {
            return teamRepository.save(newTeam);
        } catch(DataIntegrityViolationException e) {
            throw new NameAlreadyExistsException("A team with name " + name + " already exists");
        }
    }

    public Team getTeam(String name) throws TeamDoesNotExistException {
        Team team = teamRepository.findByName(name);
        if(team == null) {
            throw new TeamDoesNotExistException("Team with name " + name + " was not found");
        }
        return team;
    }

    public void addCoachToTeam(String personName, String teamName) throws CoachAlreadyOnTeamException {
        Team team = null;
        try {
            team = getTeam(teamName);
        } catch(TeamDoesNotExistException e) {
            try {
                team = createTeam(teamName);
            } catch(NameAlreadyExistsException e2) {
                // this "shouldn't" happen :D
                throw new RuntimeException("Invalid data state encountered", e2);
            }
        }
        Person coach = null;
        try {
            coach = personService.getPerson(personName);
        } catch(PersonDoesNotExistException e) {
            try {
                coach = personService.createPerson(personName);
            } catch(NameAlreadyExistsException e2) {
                // this "shouldn't" happen :D
                throw new RuntimeException("Invalid data state encountered", e2);
            }
        }
        if(team.hasCoach(coach)) {
            throw new CoachAlreadyOnTeamException("The coach " + personName + " is already on the team " + teamName);
        }
        team.addCoach(coach);
        teamRepository.save(team);
    }
}

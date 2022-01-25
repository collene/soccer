package ca.collene.soccer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import ca.collene.soccer.entities.Person;
import ca.collene.soccer.entities.Team;
import ca.collene.soccer.repositories.TeamRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PersonService personService;

    public Team createTeam(String name) throws NameAlreadyExistsException {
        Team newTeam = Team.builder().name(name)
                            .build();
        try {
            log.debug("Creating team with name " + name);
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

    public Team getOrCreateTeam(String teamName) {        
        try {
            return getTeam(teamName);            
        } catch(TeamDoesNotExistException e) {
            try {
                return createTeam(teamName);                
            } catch(NameAlreadyExistsException e2) {
                // this "shouldn't" happen
                throw new RuntimeException("Invalid data state encountered", e2);
            }
        }
    }

    public void addCoachToTeam(String personName, String teamName) throws CoachAlreadyOnTeamException {
        Team team = getOrCreateTeam(teamName);        
        Person coach = personService.getOrCreatePerson(personName);        
        if(team.hasCoach(coach)) {
            throw new CoachAlreadyOnTeamException("The coach " + personName + " is already on the team " + teamName);
        }
        team.addCoach(coach);
        teamRepository.save(team);
    }

    public void addPlayerToTeam(String personName, String teamName, int playerNumber) throws PlayerAlreadyOnTeamException, NumberAlreadyInUseException {
        Team team = getOrCreateTeam(teamName);
        Person person = personService.getOrCreatePerson(personName);
        if(team.hasPlayer(person)) {
            throw new PlayerAlreadyOnTeamException("The person " + personName + " is already on the team " + teamName);
        }
        if(team.hasPlayerWithNumber(playerNumber)) {
            throw new NumberAlreadyInUseException("The number " + playerNumber + " is already in use on the team " + teamName);
        }
        team.addPlayer(person, playerNumber);
        teamRepository.save(team);
    }
}

package ca.collene.soccer.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ca.collene.soccer.models.Tally;
import ca.collene.soccer.models.Tally.TallyType;
import ca.collene.soccer.services.GameAlreadyInTournamentException;
import ca.collene.soccer.services.GameDoesNotExistException;
import ca.collene.soccer.services.InvalidScoreException;
import ca.collene.soccer.services.TeamAlreadyInTournamentException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Entity(name = "tournament")
@Table(name = "tournament")
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
@Builder
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @ToString.Exclude     
    private Long id;

    @Column(unique = true)
    @Getter
    @Setter
    private String name;

    @ManyToMany
    @JoinTable(
        name = "tournament_team",
        joinColumns = @JoinColumn(name = "tournament_id"),
        inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    @LazyCollection(LazyCollectionOption.FALSE)
    @Getter
    @Builder.Default
    private List<Team> teams = new ArrayList<>();

    @OneToMany(
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    @JoinColumn(name = "tournament_id")    
    @LazyCollection(LazyCollectionOption.FALSE)
    @Getter
    @Builder.Default
    private List<Game> games = new ArrayList<>();

    public Tournament(String name) {
        this.name = name;
    }
        
    public void addTeam(Team team) throws TeamAlreadyInTournamentException {
        if(hasTeam(team)) {
            throw new TeamAlreadyInTournamentException("The team " + team.getName() + " is already in tournament " + name);
        }
        teams.add(team);
    }
    public boolean hasTeam(Team team) {
        log.debug("Checking if team " + team + " is in " + teams + ": " + teams.contains(team));
        return teams.stream().anyMatch(t -> t.equals(team));
    }

    public void addGame(Team team1, Team team2) throws GameAlreadyInTournamentException {
        if(hasGameWithTeams(team1, team2)) {
            throw new GameAlreadyInTournamentException("This game already exists in this tournament");
        }
        games.add(Game.builder().teams(Arrays.asList(team1, team2))
                        .build());
    }
    public boolean hasGameWithTeams(Team team1, Team team2) {
        return games.stream().anyMatch(game -> game.hasTeam(team1) && game.hasTeam(team2));
    }
    public Game getGame(Team team1, Team team2) throws GameDoesNotExistException {
        if(!hasGameWithTeams(team1, team2)) {
            throw new GameDoesNotExistException("The game with teams " + team1.getName() + " and " + team2.getName() + " does not exist in this tournament");
        }
        return games.stream()
                        .filter(game -> game.hasTeam(team1) && game.hasTeam(team2))
                        .findFirst()
                        .orElseThrow(()->new GameDoesNotExistException("The game with teams " + team1.getName() + " and " + team2.getName() + " does not exist in this tournament"));
    }
    public void scoreGame(Team team1, int team1Points, Team team2, int team2Points) throws GameDoesNotExistException, InvalidScoreException {
        Game game = getGame(team1, team2);
        game.setScore(team1, team1Points, team2, team2Points);
    }

    public List<Game> getGamesForTeam(Team team) {
        return games.stream()
                    .filter(game -> game.hasTeam(team))
                    .collect(Collectors.toList());
    }
    public List<TallyType> getTallyTypesForTeam(Team team) {
        return getGamesForTeam(team).stream()
                    .map(game -> game.getTallyTypeForTeam(team))
                    .collect(Collectors.toList());
    }

    public List<Tally> getTally() {
        log.debug("Getting tally for tournament, there are " + teams.size() + " teams");
        return teams.stream()
                    .map(team -> new Tally(team.getName(), getTallyTypesForTeam(team)))
                    .sorted(Comparator.comparingLong(tally -> ((Tally) tally).getTotal()).reversed())
                    .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if(!(o instanceof Tournament)) {
            return false;
        }
        Tournament other = (Tournament) o;
        if(this.id == null || other.id == null) {
            return Objects.equals(this.name, other.name);
        }
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, teams, games);
    }
}

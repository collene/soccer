package ca.collene.soccer.entities;

import java.util.ArrayList;
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
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.collene.soccer.models.Tally;
import ca.collene.soccer.models.Tally.TallyType;
import ca.collene.soccer.services.GameDoesNotExistException;
import ca.collene.soccer.services.InvalidScoreException;

@Entity(name = "tournament")
@Table(name = "tournament")
public class Tournament {
    @Transient
    private Logger logger = LoggerFactory.getLogger(Tournament.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToMany
    @JoinTable(
        name = "tournament_team",
        joinColumns = @JoinColumn(name = "tournament_id"),
        inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Team> teams = new ArrayList<>();

    @OneToMany(
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    @JoinColumn(name = "tournament_id")    
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Game> games = new ArrayList<>();

    public Tournament() {

    }
    public Tournament(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void addTeam(Team team) {
        teams.add(team);
    }
    public List<Team> getTeams() {
        return teams;
    }
    public boolean hasTeam(Team team) {
        return teams.contains(team);
    }

    public void addGame(Team team1, Team team2) {
        games.add(new Game.With().teams(team1, team2)
                                    .tournament(this)
                            .build());
    }
    public List<Game> getGames() {
        return games;
    }
    public boolean hasGameWithTeams(Team team1, Team team2) {
        return games.stream()
                        .filter(game -> game.hasTeam(team1) && game.hasTeam(team2))
                        .count() > 0;
    }
    public Game getGame(Team team1, Team team2) throws GameDoesNotExistException {
        if(!hasGameWithTeams(team1, team2)) {
            throw new GameDoesNotExistException("The game with teams " + team1.getName() + " and " +team2.getName() + " does not exist in this tournament");
        }
        return games.stream()
                        .filter(game -> game.hasTeam(team1) && game.hasTeam(team2))
                        .findFirst()
                        .get();
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
        logger.debug("Getting tally for tournament, there are " + teams.size() + " teams");
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
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public static class With {
        private String name;

        public With() {

        }
        public With name(String name) {
            this.name = name;
            return this;
        }

        public Tournament build() {
            return new Tournament(name);
        }
    }
}

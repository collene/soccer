package ca.collene.soccer.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ca.collene.soccer.models.Tally.TallyType;
import ca.collene.soccer.services.GameNotScoredException;
import ca.collene.soccer.services.InvalidScoreException;
import ca.collene.soccer.services.TeamNotInGameException;
import lombok.Getter;
import lombok.ToString;

@Entity(name = "game")
@Table(name = "game")
@ToString
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @ToString.Exclude
    private Long id;

    @ManyToMany
    @JoinTable(
        name = "game_team",
        joinColumns = @JoinColumn(name = "game_id"),
        inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    @Size(max = 2)
    @LazyCollection(LazyCollectionOption.FALSE)
    @Getter
    private List<Team> teams = new ArrayList<>();

    @ManyToOne
    @Getter
    private Tournament tournament;

    @Size(max = 2)
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
        name = "game_score",
        joinColumns = @JoinColumn(name = "game_id")
    )
    @MapKeyJoinColumn(name = "team_id")
    @Getter
    private Map<Team, Integer> points = new HashMap<>();    

    public Game() {

    }
    public Game(Tournament tournament, List<Team> teams) {
        this.tournament = tournament;
        this.teams = teams;
    }
    public Game(Tournament tournament, Team team1, Team team2) {
        this.tournament = tournament;
        teams.add(team1);
        teams.add(team2);
    }
    
    public boolean hasTeam(Team team) {
        return teams.contains(team);
    }    

    public boolean hasScore() {
        return !points.isEmpty();
    }
    public int getPointsForTeam(Team team) throws TeamNotInGameException, GameNotScoredException {
        if(!hasTeam(team)) {
            throw new TeamNotInGameException("The team " + team.getName() + " is not in this game");
        }
        if(!hasScore()) {
            throw new GameNotScoredException("This game has not yet been scored");
        }        
        return points.get(team);
    }
    public int getPointsForOtherTeam(Team team) throws TeamNotInGameException, GameNotScoredException {
        Team otherTeam = teams.stream().filter(t -> !t.equals(team))
                .findFirst()
                .get();
        return getPointsForTeam(otherTeam);
    }
    public void setScore(Team team1, int team1Points, Team team2, int team2Points) throws InvalidScoreException {
        if(team1Points < 0 || team2Points < 0) {
            throw new InvalidScoreException("Score points must be positive values");
        }
        points.put(team1, team1Points);
        points.put(team2, team2Points);
    }
    public TallyType getTallyTypeForTeam(Team team) {
        int thisTeamPoints;
        try {
            thisTeamPoints = getPointsForTeam(team);
            int otherTeamPoints = getPointsForOtherTeam(team);
            if(thisTeamPoints == otherTeamPoints) {
                return TallyType.TIE;
            } else if(thisTeamPoints > otherTeamPoints) {
                return TallyType.WIN;
            } else {
                return TallyType.LOSS;
            }
        } catch (TeamNotInGameException | GameNotScoredException e) {
            return TallyType.UNSCORED;
        }        
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if(!(o instanceof Game)) {
            return false;
        }
        Game other = (Game) o;
        if(this.id == null || other.id == null) {
            return Objects.equals(this.tournament, other.tournament) && this.teams.containsAll(other.teams);
        }
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tournament, teams);
    }

    public static class With {
        private Tournament tournament;
        private List<Team> teams = new ArrayList<>();

        public With() {

        }
        public With tournament(Tournament tournament) {
            this.tournament = tournament;
            return this;
        }
        public With tournament(String tournamentName) {
            this.tournament = new Tournament.With().name(tournamentName)
                                    .build();
            return this;
        }
        public With teams(List<Team> teams) {
            this.teams = teams;
            return this;
        }
        public With teams(Team team1, Team team2) {
            this.teams.add(team1);
            this.teams.add(team2);
            return this;
        }
        public With teams(String team1Name, String team2Name) {
            this.teams.add(new Team.With().name(team1Name)
                                .build());
            this.teams.add(new Team.With().name(team2Name)
                                .build());
            return this;
        }       

        public Game build() {
            return new Game(tournament, teams);
        }
    }
}

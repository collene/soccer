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
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ca.collene.soccer.models.Tally.TallyType;
import ca.collene.soccer.services.GameNotScoredException;
import ca.collene.soccer.services.InvalidScoreException;
import ca.collene.soccer.services.TeamNotInGameException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "game")
@Table(name = "game")
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
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
    @Builder.Default
    private List<Team> teams = new ArrayList<>();

    @Size(max = 2)
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
        name = "game_score",
        joinColumns = @JoinColumn(name = "game_id")
    )
    @MapKeyJoinColumn(name = "team_id")
    @Getter
    @Builder.Default
    @EqualsAndHashCode.Exclude
    private Map<Team, Integer> points = new HashMap<>();    
    
    public Game(List<Team> teams) {        
        this.teams = teams;
    }
    public Game(Team team1, Team team2) {        
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
            return Objects.equals(this.teams, other.teams)
                && Objects.equals(this.points, other.points);       
        }
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, teams, points);
    }    
}

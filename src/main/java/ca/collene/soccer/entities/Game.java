package ca.collene.soccer.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity(name = "game")
@Table(name = "game")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
        name = "game_team",
        joinColumns = @JoinColumn(name = "game_id"),
        inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    @Size(max = 2)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Team> teams = new ArrayList<>();

    @ManyToOne
    private Tournament tournament;

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
    
    public Tournament getTournament() {
        return tournament;
    }
    public List<Team> getTeams() {
        return teams;
    }
    public boolean hasTeam(Team team) {
        return teams.contains(team);
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

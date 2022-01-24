package ca.collene.soccer.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity(name = "tournament")
@Table(name = "tournament")
public class Tournament {
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

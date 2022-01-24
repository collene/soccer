package ca.collene.soccer.entities;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "player")
@Table(name = "player")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Team team;

    private int number;

    @ManyToOne
    private Person person;

    public Player() {

    }

    public Player(Person person, Team team, int number) {
        this.person = person;
        this.team = team;
        this.number = number;
    }

    public Long getId() {
        return id;
    }

    public Team getTeam() {
        return team;
    }
    public void setTeam(Team team) {
        this.team = team;
    }

    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }

    public Person getPerson() {
        return person;
    }
    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if(!(o instanceof Player)) {
            return false;
        }
        Player other = (Player) o;
        if(this.id == null || other.id == null) {
            return Objects.equals(this.person, other.person) && Objects.equals(this.team, other.team) && this.number == other.number;
        }
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, person, team);
    }

    public static class With {
        private Person person;
        private Team team;
        private int number;

        public With() {

        }
        public With person(Person person) {
            this.person = person;
            return this;
        }

        public With person(String personName) {
            this.person = new Person.With().name(personName)
                            .build();
            return this;
        }

        public With team(Team team) {
            this.team = team;
            return this;
        }

        public With team(String teamName) {
            this.team = new Team.With().name(teamName)
                            .build();
            return this;
        }

        public With number(int number) {
            this.number = number;
            return this;
        }

        public Player build() {
            return new Player(person, team, number);
        }
    }
}
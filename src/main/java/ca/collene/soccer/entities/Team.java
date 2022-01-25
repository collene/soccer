package ca.collene.soccer.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

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

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "team")
@Table(name = "team")
@ToString
public class Team {
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
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(
        name = "team_coach",
        joinColumns = @JoinColumn(name = "team_id"),
        inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    @Getter
    private List<Person> coaches = new ArrayList<>();

    @OneToMany(
        mappedBy = "team",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )    
    @LazyCollection(LazyCollectionOption.FALSE)
    @Getter
    private List<Player> players = new ArrayList<>();

    public Team() {

    }

    public Team(String name) {
        this.name = name;
    }
    
    public void addCoach(Person person) {
        this.coaches.add(person);
    }
    public boolean hasCoach(Person person) {
        return coaches.contains(person);
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }
    public boolean hasPlayer(Player player) {
        return players.contains(player);
    }
    public boolean hasPlayer(Person person) {
        return hasPlayerMatching(player -> Objects.equals(player.getPerson(), person));        
    }
    public boolean hasPlayerWithNumber(int number) {
        return hasPlayerMatching(player -> player.getNumber() == number);
    }    

    private boolean hasPlayerMatching(Predicate<Player> lambda) {
        return players.stream()
                    .filter(lambda)
                    .count() > 0;
    }

    public Player addPlayer(Person person, int number) {
        Player player = new Player(person, this, number);
        this.players.add(player);
        return player;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if(!(o instanceof Team)) {
            return false;
        }
        Team other = (Team) o;
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

        public Team build() {
            return new Team(name);
        }
    }
}

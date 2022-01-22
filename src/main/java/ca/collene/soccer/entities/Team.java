package ca.collene.soccer.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity(name = "team")
@Table(name = "team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "team_coach",
        joinColumns = @JoinColumn(name = "team_id"),
        inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    private List<Person> coaches = new ArrayList<>();

    public Team() {

    }

    public Team(String name) {
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
    public void addCoach(Person person) {
        this.coaches.add(person);
    }
    public List<Person> getCoaches() {
        return coaches;
    }
    public boolean hasCoach(Person person) {
        return coaches.contains(person);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if(!(o instanceof Team)) {
            return false;
        }
        Team other = (Team) o;
        return this.id == other.id && Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

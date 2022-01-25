package ca.collene.soccer.entities;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "player")
@Table(name = "player")
@ToString
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @ToString.Exclude
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @Getter
    @Setter
    private Team team;

    @Getter
    @Setter
    private int number;

    @ManyToOne
    @Getter
    @Setter
    private Person person;

    public Player(Person person, Team team, int number) {
        this.person = person;
        this.team = team;
        this.number = number;
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
}

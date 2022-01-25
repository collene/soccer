package ca.collene.soccer.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "person")
@Table(name = "person")
@ToString
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @ToString.Exclude
    private Long id;

    @Column(unique = true)
    @Getter
    @Setter
    private String name;

    public Person() {

    }
    public Person(String name) {
        this.name = name;
    }    

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if(!(o instanceof Person)) {
            return false;
        }
        Person other = (Person) o;    
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

        public Person build() {
            return new Person(name);
        }
    }
}

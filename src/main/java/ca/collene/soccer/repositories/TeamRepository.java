package ca.collene.soccer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.collene.soccer.entities.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    public Team findByName(String name);
}

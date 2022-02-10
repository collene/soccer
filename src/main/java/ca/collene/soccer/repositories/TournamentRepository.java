package ca.collene.soccer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.collene.soccer.entities.Tournament;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    Tournament findByName(String name);
}

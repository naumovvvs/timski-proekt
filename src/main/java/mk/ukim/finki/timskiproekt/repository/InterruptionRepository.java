package mk.ukim.finki.timskiproekt.repository;

import mk.ukim.finki.timskiproekt.model.Interruption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterruptionRepository extends JpaRepository<Interruption, Long> {

}

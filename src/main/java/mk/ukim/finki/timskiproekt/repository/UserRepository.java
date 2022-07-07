package mk.ukim.finki.timskiproekt.repository;

import mk.ukim.finki.timskiproekt.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
}

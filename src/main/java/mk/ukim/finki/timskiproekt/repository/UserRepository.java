package mk.ukim.finki.timskiproekt.repository;

import mk.ukim.finki.timskiproekt.model.AppUser;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
@Primary
public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
}

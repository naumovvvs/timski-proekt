package mk.ukim.finki.timskiproekt.repository;

import mk.ukim.finki.timskiproekt.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}

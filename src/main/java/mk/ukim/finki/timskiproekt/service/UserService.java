package mk.ukim.finki.timskiproekt.service;

import mk.ukim.finki.timskiproekt.model.*;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    AppUser saveUser(String name, String username, String password, String email,
                     LocalDate birthDate, Role role, Long index);
    Role saveRole(Role role);
    void addRoleToUser(String username, String rollName);
    AppUser getUser(String username);
    List<AppUser> getAllUsers();
    List<Course> getAllCoursesByProfessor(String username);
}

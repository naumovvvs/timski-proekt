package mk.ukim.finki.timskiproekt.service;

import mk.ukim.finki.timskiproekt.model.*;
import mk.ukim.finki.timskiproekt.model.dto.RoleDTO;
import mk.ukim.finki.timskiproekt.model.dto.SaveUserDTO;

import java.util.List;

public interface UserService {
    AppUser saveUser(SaveUserDTO saveUserDTO);
    Role saveRole(RoleDTO role);
    void addRoleToUser(String username, String rollName);
    AppUser getUser(String username);
    List<AppUser> getAllUsers();
    List<Course> getAllCoursesByProfessor(String username);
}

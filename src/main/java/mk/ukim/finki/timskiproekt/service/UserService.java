package mk.ukim.finki.timskiproekt.service;

import mk.ukim.finki.timskiproekt.model.AppUser;
import mk.ukim.finki.timskiproekt.model.Role;

import java.util.List;

public interface UserService {
    AppUser saveUser(AppUser appUser);
    Role saveRole(Role role);
    void addRoleToUser(String username, String rollName);
    AppUser getUser(String username);
    List<AppUser> getAllUsers();
}

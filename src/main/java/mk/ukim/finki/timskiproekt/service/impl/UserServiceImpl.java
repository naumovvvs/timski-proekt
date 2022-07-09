package mk.ukim.finki.timskiproekt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.timskiproekt.model.*;
import mk.ukim.finki.timskiproekt.repository.*;
import mk.ukim.finki.timskiproekt.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    //TODO: add more logic for validating username, checking for duplicates, checking for null objects...

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = this.userRepository.findByUsername(username);
        if (user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User {} found in the database", username);
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public AppUser saveUser(String name, String username, String password, String email,
                            LocalDate birthDate, Role role, Long index) {
        log.info("Saving new user {} to database", username);

        switch (role.getName()) {
            case "ROLE_STUDENT":
                return this.studentRepository.save(new Student(name, username,
                        passwordEncoder.encode(password), email, birthDate, index, role));
            case "ROLE_PROFESSOR":
                return this.professorRepository.save(new Professor(name, username,
                        passwordEncoder.encode(password), email, birthDate, role));
            case "ROLE_ADMIN":
                return this.adminRepository.save(new Admin(name, username,
                        passwordEncoder.encode(password), email, birthDate, role));
            default:
                // TODO: If null return error on frontend
                return null;
        }
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {} to database", role.getName());
        return this.roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String rollName) {
        log.info("Adding role {} to user {}", rollName, username);
        AppUser appUser = this.userRepository.findByUsername(username);
        Role role = this.roleRepository.findByName(rollName);
        appUser.getRoles().add(role);
    }

    @Override
    public AppUser getUser(String username) {
        log.info("Fetching user {}", username);
        return this.userRepository.findByUsername(username);
    }

    @Override
    public List<AppUser> getAllUsers() {
        log.info("Fetching all users");
        return this.userRepository.findAll();
    }

    @Override
    public List<Course> getAllCoursesByProfessor(String username) {
        Professor professor = (Professor) this.professorRepository.findByUsername(username);
        log.info("Getting all courses by professor: {}", professor.getName());
        return new ArrayList<>(professor.getCourses());
    }
}

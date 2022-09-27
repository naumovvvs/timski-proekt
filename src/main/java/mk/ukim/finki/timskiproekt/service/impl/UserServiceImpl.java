package mk.ukim.finki.timskiproekt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.timskiproekt.model.*;
import mk.ukim.finki.timskiproekt.model.dto.RoleDTO;
import mk.ukim.finki.timskiproekt.model.dto.SaveUserDTO;
import mk.ukim.finki.timskiproekt.repository.*;
import mk.ukim.finki.timskiproekt.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public AppUser saveUser(SaveUserDTO saveUserDTO) {
        log.info("Saving new user {} to database", saveUserDTO.getUsername());

        String accType = saveUserDTO.getAccountType().getName();

        //Check for duplicate usernames
        if(userRepository.findByUsername(saveUserDTO.getUsername()) != null) {
            log.info("A user with the same username already exists! username: {}", saveUserDTO.getUsername());
            throw new RuntimeException("Error saving user");
        }

        switch (accType) {
            case "ROLE_STUDENT": {

                AppUser user = this.studentRepository.save(new Student(saveUserDTO.getName(), saveUserDTO.getUsername(),
                        passwordEncoder.encode(saveUserDTO.getPassword()), saveUserDTO.getEmail(),
                        saveUserDTO.getBirthDate()));

                return addRolesToUser(user, saveUserDTO.getRoles());
            }
            case "ROLE_PROFESSOR": {
                AppUser user = this.professorRepository.save(new Professor(saveUserDTO.getName(), saveUserDTO.getUsername(),
                        passwordEncoder.encode(saveUserDTO.getPassword()), saveUserDTO.getEmail(),
                        saveUserDTO.getBirthDate()));

                return addRolesToUser(user, saveUserDTO.getRoles());
            }
            case "ROLE_ADMIN": {
                AppUser user = this.adminRepository.save(new Admin(saveUserDTO.getName(), saveUserDTO.getUsername(),
                        passwordEncoder.encode(saveUserDTO.getPassword()), saveUserDTO.getEmail(),
                        saveUserDTO.getBirthDate()));

                return addRolesToUser(user, saveUserDTO.getRoles());
            }
            default:
                throw new RuntimeException("No user type selected");
        }
    }

    private AppUser addRolesToUser(AppUser user, List<RoleDTO> rolesDTO ) {
        List<Role> userRoles = user.getRoles();

        for (RoleDTO r : rolesDTO) {
            Role role = roleRepository.findByName(r.getName());
            userRoles.add(role);
        }

        user.setRoles(userRoles);

        return this.userRepository.save(user);
    }

    @Override
    public Role saveRole(RoleDTO role) {
        log.info("Saving new role {} to database", role.getName());
        return this.roleRepository.save(new Role(role.getName()));
    }

    @Override
    public void addRoleToUser(String username, String rollName) {
        log.info("Adding role {} to user {}", rollName, username);
        AppUser appUser = this.userRepository.findByUsername(username);
        Role role = this.roleRepository.findByName(rollName);

        if(appUser!=null && role!=null && !appUser.getRoles().contains(role)) {
            appUser.getRoles().add(role);
        }
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

package mk.ukim.finki.timskiproekt;

import mk.ukim.finki.timskiproekt.model.Role;
import mk.ukim.finki.timskiproekt.model.dto.RoleDTO;
import mk.ukim.finki.timskiproekt.model.dto.SaveUserDTO;
import mk.ukim.finki.timskiproekt.service.UserService;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class TimskiProektApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimskiProektApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserService userService) {
        // Will run after the application has initialized
        return args -> {
            Role studentRole = userService.saveRole(new RoleDTO("ROLE_STUDENT"));
            Role professorRole = userService.saveRole(new RoleDTO("ROLE_PROFESSOR"));
            Role adminRole = userService.saveRole(new RoleDTO("ROLE_ADMIN"));

            userService.saveUser(new SaveUserDTO("Strasho Naumov", "naumovs", "naumovs123",
                    "strashe_n@test.com", LocalDate.now(),
                    new RoleDTO("ROLE_STUDENT"), List.of(new RoleDTO("ROLE_STUDENT"))));

            userService.saveUser(new SaveUserDTO("Jelena Ognjanoska", "ognj", "ognj123",
                    "jelena_o@test.com", LocalDate.now(),
                    new RoleDTO("ROLE_STUDENT"), List.of(new RoleDTO("ROLE_STUDENT"))));

            userService.saveUser(new SaveUserDTO("Kristijan Isajlovski", "kiko", "kiko123",
                    "kiko_i@test.com", LocalDate.now(),
                    new RoleDTO("ROLE_STUDENT"), List.of(new RoleDTO("ROLE_STUDENT"))));

            userService.saveUser(new SaveUserDTO("Sasho Gramatikov", "gramatikov", "gramatikov123",
                    "sasho_g@test.com", LocalDate.now(),
                    new RoleDTO("ROLE_PROFESSOR"), List.of(new RoleDTO("ROLE_PROFESSOR"))));

            userService.saveUser(new SaveUserDTO("Admin Adminovski", "admin", "admin123",
                    "admin@test.com", LocalDate.now(),
                    new RoleDTO("ROLE_ADMIN"), List.of(new RoleDTO("ROLE_ADMIN"))));

//            userService.addRoleToUser("naumovs", "ROLE_STUDENT");
//            userService.addRoleToUser("ognj", "ROLE_STUDENT");
//            userService.addRoleToUser("gramatikov", "ROLE_PROFESSOR");
//            userService.addRoleToUser("kiko", "ROLE_ADMIN");
        };
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}

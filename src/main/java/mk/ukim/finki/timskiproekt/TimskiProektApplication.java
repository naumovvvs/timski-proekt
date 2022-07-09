package mk.ukim.finki.timskiproekt;

import mk.ukim.finki.timskiproekt.model.AppUser;
import mk.ukim.finki.timskiproekt.model.Role;
import mk.ukim.finki.timskiproekt.service.UserService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;

@SpringBootApplication
public class TimskiProektApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimskiProektApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserService userService) {
        // Will run after the application has initialized
        return args -> {
            Role studentRole = userService.saveRole(new Role(null, "ROLE_STUDENT"));
            Role professorRole = userService.saveRole(new Role(null, "ROLE_PROFESSOR"));
            Role adminRole = userService.saveRole(new Role(null, "ROLE_ADMIN"));

            userService.saveUser("Strasho Naumov", "naumovs", "naumovs123",
                    "strashe_n@test.com", LocalDate.now(), studentRole, 183050L);
            userService.saveUser("Jelena Ognjanoska", "ognj", "ognj123",
                    "jelena_o@test.com", LocalDate.now(), studentRole, 183005L);
            userService.saveUser("Kristijan Isajlovski", "kiko", "kiko123",
                    "kiko_i@test.com", LocalDate.now(), studentRole, 183111L);
            userService.saveUser("Sasho Gramatikov", "gramatikov", "gramatikov123",
                    "sasho_g@test.com", LocalDate.now(), professorRole, null);
            userService.saveUser("Admin Adminovski", "admin", "admin123",
                    "admin@test.com", LocalDate.now(), adminRole, null);

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

}

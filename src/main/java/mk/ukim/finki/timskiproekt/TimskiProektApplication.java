package mk.ukim.finki.timskiproekt;

import mk.ukim.finki.timskiproekt.model.AppUser;
import mk.ukim.finki.timskiproekt.model.Role;
import mk.ukim.finki.timskiproekt.service.UserService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
            userService.saveRole(new Role(null, "ROLE_STUDENT"));
            userService.saveRole(new Role(null, "ROLE_PROFESSOR"));
            userService.saveRole(new Role(null, "ROLE_ADMIN"));

            userService.saveUser(new AppUser(null, "Strasho Naumov", "naumovs", "naumovs123",
                    new ArrayList<>()));
            userService.saveUser(new AppUser(null, "Jelena Ognjanoska", "ognj", "ognj123",
                    new ArrayList<>()));
            userService.saveUser(new AppUser(null, "Sasho Gramatikov", "gramatikov", "gramatikov123",
                    new ArrayList<>()));
            userService.saveUser(new AppUser(null, "Kristijan Isajlovski", "kiko", "kiko123",
                    new ArrayList<>()));

            userService.addRoleToUser("naumovs", "ROLE_STUDENT");
            userService.addRoleToUser("ognj", "ROLE_STUDENT");
            userService.addRoleToUser("gramatikov", "ROLE_PROFESSOR");
            userService.addRoleToUser("kiko", "ROLE_ADMIN");
        };
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

}

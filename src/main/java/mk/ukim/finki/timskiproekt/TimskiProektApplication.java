package mk.ukim.finki.timskiproekt;

import mk.ukim.finki.timskiproekt.model.Course;
import mk.ukim.finki.timskiproekt.model.Role;
import mk.ukim.finki.timskiproekt.model.Room;
import mk.ukim.finki.timskiproekt.model.dto.*;
import mk.ukim.finki.timskiproekt.model.enums.Semester;
import mk.ukim.finki.timskiproekt.service.CourseService;
import mk.ukim.finki.timskiproekt.service.RoomService;
import mk.ukim.finki.timskiproekt.service.StudentService;
import mk.ukim.finki.timskiproekt.service.UserService;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@SpringBootApplication
public class TimskiProektApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimskiProektApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserService userService, CourseService courseService, StudentService studentService, RoomService roomService) {
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
            Course km = courseService.createCourse(new CourseDTO("Компјутерски Мрежи", "KM001", "https://media.springernature.com/w580h326/nature-cms/uploads/collections/Networks-Collection-img-final-f2c265a59e457f48645e2aa3ff90e942.jpg", Semester.WINTER));
            Course tp = courseService.createCourse(new CourseDTO("Тимски Проект", "TP001", "https://images-preview.moj-posao.net/article/db/adaa392bd-resize-820x0x100.jpg", Semester.WINTER));
            courseService.createCourse(new CourseDTO("Бази на податоци", "DB001", "https://miro.medium.com/max/1400/1*mLqtKV1FjUg-WKlLW-cXjQ.jpeg", Semester.WINTER));
            courseService.createCourse(new CourseDTO("Веб дизајн", "WD001", "https://99designs-blog.imgix.net/blog/wp-content/uploads/2018/09/WHAT-IS-WEB-DESIGN.jpg?auto=format&q=60&w=1860&h=1395&fit=crop&crop=faces", Semester.SUMMER));
            studentService.addCourseToStudent(km, 3L);
            Room s1 = roomService.create(new SaveRoomDto("Испит - Компјутерски Мрежи", LocalDateTime.now(), LocalDateTime.of(2022, Month.JULY, 25, 17, 40), 1L, 4L ));
            Room s2 = roomService.create(new SaveRoomDto("Предавања", LocalDateTime.now(), LocalDateTime.of(2022, Month.JULY, 25, 17, 40), 1L, 4L ));
            courseService.addRoomToCourse(s1, "KM001");
            courseService.addRoomToCourse(s2, "KM001");
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

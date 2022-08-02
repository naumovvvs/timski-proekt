package mk.ukim.finki.timskiproekt.web;

import lombok.AllArgsConstructor;
import mk.ukim.finki.timskiproekt.model.AppUser;
import mk.ukim.finki.timskiproekt.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping({"/", "/home"})
@AllArgsConstructor
public class HomeController {

    private final UserService userService;

    @GetMapping
    public String getHomePage(){
        return "index";
    }
    @GetMapping("/subject")
    public String getSubjectPage() { return "subject"; }

    @GetMapping("/room")
    public String getRoomPage(@RequestParam(required = false) Long room) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AppUser appUser = this.userService.getUser(username);
        return "room";
    }
}

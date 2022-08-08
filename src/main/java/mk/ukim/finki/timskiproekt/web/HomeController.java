package mk.ukim.finki.timskiproekt.web;

import lombok.AllArgsConstructor;
import mk.ukim.finki.timskiproekt.service.RoomService;
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

    private final RoomService roomService;

    @GetMapping
    public String getHomePage() {
        return "index";
    }

    @GetMapping("/subject")
    public String getSubjectPage() {
        return "subject";
    }

    @GetMapping("/room")
    public String getRoomPage(@RequestParam Long room, @RequestParam Long student) {
        if (!this.roomService.checkIfStudentIsAllowed(room, student)) {
            return "redirect:/home";
        }
        return "room";
    }
}

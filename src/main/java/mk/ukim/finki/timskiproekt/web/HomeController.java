package mk.ukim.finki.timskiproekt.web;

import lombok.AllArgsConstructor;
import mk.ukim.finki.timskiproekt.model.Room;
import mk.ukim.finki.timskiproekt.model.enums.RoomStatus;
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
    public String getRoomPage(@RequestParam Long room, @RequestParam Long student, @RequestParam Boolean isProfessor) {
        Room roomObject = this.roomService.getRoomById(room);
        if (roomObject.getStatus().equals(RoomStatus.CLOSED) && !isProfessor) {
            return "redirect:/access-denied";
        }
        if (!isProfessor && !this.roomService.checkIfStudentIsAllowed(roomObject, student)) {
            return "redirect:/access-denied";
        }
        return "room";
    }

    @GetMapping("/access-denied")
    public String getAccessDeniedPage() {
        return "access-denied";
    }

    @GetMapping("/not-found")
    public String getNotFoundPage() {
        return "not-found";
    }
}

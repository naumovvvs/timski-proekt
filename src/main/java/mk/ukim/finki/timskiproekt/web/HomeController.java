package mk.ukim.finki.timskiproekt.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping({"/", "/home"})
public class HomeController {

    @GetMapping
    public String getHomePage(){
        return "index";
    }
}

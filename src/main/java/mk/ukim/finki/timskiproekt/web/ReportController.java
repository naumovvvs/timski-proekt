package mk.ukim.finki.timskiproekt.web;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping({"/report"})
@AllArgsConstructor
public class ReportController {

    @GetMapping("/{roomId}")
    public String getReports(@PathVariable Long roomId) {
        return "report";
    }
}
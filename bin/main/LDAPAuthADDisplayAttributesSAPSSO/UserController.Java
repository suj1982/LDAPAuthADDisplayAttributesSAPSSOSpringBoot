import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/user")
    public String getUserAttributes(Model model) {
        // Add any logic to retrieve additional user details if needed
        return "user";
    }
}

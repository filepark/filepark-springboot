package controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class PageController {
	@GetMapping("/home")
	public String home(HttpServletRequest request, HttpSession session, Model model) {
		return "home";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/signup")
	public String signup() {
		return "signup";
	}

	@GetMapping("/profile")
	public String profile() {
		return "profile";
	}

	@GetMapping("/groups/{groupId}")
	public String groups(@PathVariable int groupId, Model model) {
		model.addAttribute("groupId", groupId);
		return "groups";
	}

	@GetMapping("/groups/setting")
	public String method() {
		return "groups/setting";
	}
}

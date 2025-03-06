package controller;

import dto.KakaoUserInfoResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.KakaoService;
import service.UsersService;

import java.io.IOException;

@Controller
public class PageController {
    @Value("${kakao.client_id}")
    private String client_id;
    @Value("${kakao.redirect_uri}")
    private String redirect_uri;
    @Autowired
    KakaoService kakaoService;
    @Autowired
    UsersService usersService;

    @GetMapping("/home")
    public String hello() {
        return "home";
    }

    @GetMapping("/login")
    public String login(Model model) {
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + client_id + "&redirect_uri=" + redirect_uri;
        model.addAttribute("location", location);

        return "login";
    }

    @GetMapping("/login/oauth2/code/kakao")
    public String callback(@RequestParam("code") String code, HttpSession session) throws IOException {
        String accessToken = kakaoService.getAccessTokenFromKakao(code);
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);
        ResponseEntity<?> result = new ResponseEntity<>(HttpStatus.OK);
        if (result.getStatusCode() == HttpStatus.OK) {
            String nickname = userInfo.getKakaoAccount().getProfile().getNickName();
            String profileImage = userInfo.getKakaoAccount().getProfile().getProfileImageUrl();

            session.setAttribute("loginstatus", "ok");
            session.setAttribute("nickname", nickname);
            session.setAttribute("profileimage", profileImage);
            if (usersService.chkMember(nickname, profileImage) == 0) {
                usersService.signUp(nickname, profileImage);
                System.out.println(nickname+"님 회원가입");
            }
        }
        return "redirect:/home";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/groups")
    public String groups() {
        return "groups";
    }

    @GetMapping("/groups/setting")
    public String method() {
        return "groups/setting";
    }
}

package controller;

import dto.DirectoryDTO;
import dto.GroupsDTO;
import dto.KakaoUserInfoResponseDto;
import dto.NaverProfileDto;
import dto.UsersDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import service.DirectoryService;
import service.GroupsService;
import service.KakaoService;
import service.NaverService;
import service.ObjectStorageService;
import service.UsersService;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

@Controller
public class PageController {
	@Value("${kakao.client_id}")
	private String kakaoClientId;
	@Value("${kakao.redirect_uri}")
	private String kakaoRedirectUri;
	@Value("${naver.client_id}")
	private String naverClientId;
	@Value("${naver.redirect_uri}")
	private String naverRedirectUri;
	@Value("${kakao.logout_uri}")
	private String logoutUri;
	@Autowired
	KakaoService kakaoService;
	@Autowired
	UsersService usersService;
	@Autowired
	private NaverService naverService;
	@Value("${ncp.bucketName}")
	private String ncpBucketName;
	@Value("${ncp.endPoint}")
	private String ncpEndPoint;

	@Autowired
	GroupsService groupsService;
	@Autowired
	DirectoryService directoryService;
	@Autowired
	ObjectStorageService objectStorageService;

	@GetMapping("/home")
	public String home(HttpSession session) throws ParseException {
		String logoutLocation = "https://kauth.kakao.com/oauth/logout";
		String provider = (String) session.getAttribute("provider");
		if (provider == "NAVER") {
			int userId = (int) session.getAttribute("userId");
			UsersDTO dto = usersService.getUserById(userId);
			session.setAttribute("name", dto.getName());
			session.setAttribute("profileImage", dto.getProfileImage());
			session.setAttribute("logout_uri", "/logout/oauth2/naver" + "?accessToken=" + session.getAttribute("accessToken"));
		} else if (provider == "KAKAO") {
			session.setAttribute("logout_uri", logoutLocation + "?client_id=" + kakaoClientId + "&logout_redirect_uri=" + logoutUri);
		}

		return "home";
	}

	@GetMapping("/login")
	public String login(Model model) {
		String kakaoLogin = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + kakaoClientId + "&redirect_uri="
				+ kakaoRedirectUri;
		String naverLogin = "https://nid.naver.com/oauth2.0/authorize?response_type=code&state=STATE_STRING&client_id=" + naverClientId
				+ "&redirect_uri=" + naverRedirectUri;
		model.addAttribute("kakaoLogin", kakaoLogin);
		model.addAttribute("naverLogin", naverLogin);
		return "login";
	}

	@GetMapping("/groups/{hashedId}")
	public String groupsHashedId(@PathVariable String hashedId) {
		GroupsDTO group = groupsService.readGroupByHashedId(hashedId);
		if (group == null) {
			return "error/404";
		}
		return "redirect:/groups/" + hashedId + "/";
	}

	@GetMapping("/groups/{hashedId}/**")
	public String groupsHashedIdPath(@PathVariable String hashedId, Model model, HttpServletRequest request) {
		GroupsDTO group = groupsService.readGroupByHashedId(hashedId);
		if (group == null) {
			return "error/404";
		}
		String prefix = "/groups/" + hashedId + "/";
		String currentPath = request.getRequestURI().substring(prefix.length());
		String urlPath = URLDecoder.decode(currentPath, StandardCharsets.UTF_8);
		DirectoryDTO directory = directoryService.readDirectoryByDirectoryPath(group.getId(), urlPath);
		System.out.println("page directory" + directory);
		if (directory == null) {
			return "error/404";
		}
		
		model.addAttribute("hashedId", hashedId);
		model.addAttribute("optimizerPrefix", objectStorageService.getOptimizerEndPoint() + "/files/");
		model.addAttribute("currentPath", currentPath);
		model.addAttribute("directoryId", directory.getId());
		return "groups";
	}

    @GetMapping("/groups/{groupId}/setting")
    public String method(@PathVariable int groupId, Model model) {
    	model.addAttribute("groupId", groupId);
        return "groupSetting";
    }

	@GetMapping("/login/oauth2/code/kakao")
	public String kakaoCallback(@RequestParam String code, HttpSession session) throws IOException {
		String accessToken = kakaoService.getAccessTokenFromKakao(code);
		System.out.println("kakaoAccessToken = " + accessToken);
		KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);
		ResponseEntity<?> result = new ResponseEntity<>(HttpStatus.OK);
		if (result.getStatusCode() == HttpStatus.OK) {
			String nickname = userInfo.getKakaoAccount().getProfile().getNickName();
			String profileImage = userInfo.getKakaoAccount().getProfile().getProfileImageUrl();

            session.setAttribute("userId", "0");
            session.setAttribute("name", nickname);
            session.setAttribute("profileImage", profileImage);
            session.setAttribute("provider", "KAKAO");
            if (usersService.chkMemberKakao(nickname, profileImage) == 0) {
                usersService.signUp(nickname, profileImage,"", "KAKAO");
                System.out.println(nickname + "님 회원가입");
            }
        }
        return "redirect:/home";
    }

	@GetMapping("/logout/oauth2/kakao")
	public String logoutKakao(HttpSession session) {
		session.invalidate();

		return "redirect:/home";
	}

	@GetMapping("/login/oauth2/code/naver")
	public String naverCallback(@RequestParam String code, @RequestParam String state, HttpSession session) throws IOException {
		// 접근 토큰 발급 요청
		String accessToken = naverService.getAccessToken(code, state);
		System.out.println("Naver accessToken = " + accessToken);

        //사용자 정보 받기
        NaverProfileDto userInfo = naverService.getUserInfo(accessToken);
        ResponseEntity<?> result = new ResponseEntity<>(HttpStatus.OK);
        if (result.getStatusCode() == HttpStatus.OK) {
            String name = userInfo.getName();
            String profileImage = userInfo.getProfileImage();
            String email = userInfo.getEmail();
            if (usersService.chkMember(email) == 0) {
                usersService.signUp(name, profileImage, email ,"NAVER");
                System.out.println(name + "님 회원가입");
            }
            UsersDTO usersdto = usersService.getUserByEmail(email);
            session.setAttribute("accessToken", accessToken);
            session.setAttribute("userId", usersdto.getId());
            session.setAttribute("provider", "NAVER");
            session.setAttribute("createdAt",usersdto.getCreatedAt());
            session.setAttribute("storageUrl",ncpEndPoint+"/"+ncpBucketName);
        }
        return "redirect:/home";
    }

	@GetMapping("/logout/oauth2/naver")
	public String logoutNaver(@RequestParam String accessToken, HttpSession session) {
		naverService.logout(accessToken);
		session.invalidate();
		return "redirect:/home"; // 로그아웃 후 리다이렉트
	}

	@GetMapping("/signup")
	public String signup() {
		return "signup";
	}

    @GetMapping("/profile")
    public String profile(@RequestParam int id) {
        return "profile";
    }

}

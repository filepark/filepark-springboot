package controller;

import dto.ChatLogDTO;
import dto.UsersDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import service.ChatLogService;
import service.ObjectStorageService;
import service.UsersService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {
	@Autowired
	ObjectStorageService storageService;
	@Autowired
	UsersService usersService;
    @Autowired
    private ChatLogService chatLogService;

	@GetMapping("")
	public String test() {
		return "v1";
	}

	@PostMapping("updateProfile")
	public ResponseEntity<?> updateProfile(@RequestParam(value = "upload", required = false) MultipartFile upload,
										   @RequestParam String name,
										   @RequestParam String description,
										   HttpSession session) {

		UsersDTO dto = new UsersDTO();
		int userId = (int) session.getAttribute("userId");
		dto.setName(name);
		dto.setDescription(description);
		dto.setId(userId);
		dto.setProfileImage(usersService.getUserById(userId).getProfileImage());
		if (upload != null) {
			String filename = storageService.uploadFile(storageService.getBucketName(),"profile",upload);
			dto.setProfileImage(filename);
		}
		usersService.updateUserById(dto);
		session.setAttribute("name",dto.getName());
		session.setAttribute("description",dto.getDescription());
		session.setAttribute("profileImage",dto.getProfileImage());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/readUser")
	public ResponseEntity<?> readUser(@RequestParam(value = "id", required = true) int id) throws ParseException {
		UsersDTO dto = usersService.getUserById(id);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		dto.setCreatedAt(sdf.format(sdf.parse(dto.getCreatedAt())));
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@GetMapping("/chatLog/{groupId}")
	public List<ChatLogDTO> chatLog(@PathVariable int groupId) {
		return chatLogService.getChatLog(groupId);
	}
}

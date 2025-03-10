package controller;

import dto.UsersDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import service.ObjectStorageService;
import service.UsersService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {
	@Autowired
	ObjectStorageService storageService;
	@Autowired
	UsersService usersService;

	@GetMapping
	public String test() {
		return "v1";
	}

	@PostMapping("updateProfile")
	public ResponseEntity<?> updateProfile(@RequestParam(value = "upload", required = false) MultipartFile upload,
			@RequestParam String name, @RequestParam String description, HttpSession session) {

		UsersDTO dto = new UsersDTO();

		dto.setName(name);
		dto.setDescription(description);
		dto.setId((Integer) session.getAttribute("userId"));
		dto.setProfileImage(session.getAttribute("profileImage").toString());
		if (upload != null) {
			String filename = storageService.uploadFile(storageService.getBucketName(), "profile", upload);
			dto.setProfileImage(filename);
			session.setAttribute("profileImage", filename);
		}
		usersService.updateUserById(dto);
		session.setAttribute("name", dto.getName());
		session.setAttribute("description", dto.getDescription());
		return new ResponseEntity<>(HttpStatus.OK);
	}
}

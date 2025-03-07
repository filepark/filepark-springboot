package controller;

import dto.UsersDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import service.ObjectStorageService;
import service.UsersService;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {
	@Autowired
	ObjectStorageService storageService;
	@Autowired
	UsersService usersService;

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
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/readUser")
	public ResponseEntity<?> readUser(@RequestParam(value = "id", required = true) int id) throws ParseException {
		UsersDTO dto = usersService.getUserById(id);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		dto.setCreatedAt(sdf.format(sdf.parse(dto.getCreatedAt())));
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}
}

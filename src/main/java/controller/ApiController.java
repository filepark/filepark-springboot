package controller;

import config.NcpObjectStorageService;
import dto.UsersDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import service.UsersService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {
	@Autowired
	NcpObjectStorageService storageService;
	@Autowired
	UsersService usersService;

	private String bucketName = "bitcamp-bucket";

	@GetMapping("")
	public String test() {
		return "v1";
	}

	@PostMapping("updateProfile")
	public ResponseEntity<?> updateProfile(@RequestParam("upload") MultipartFile upload,
										   @RequestParam String name,
										   @RequestParam String description,
										   HttpSession session) {

//		String filename = storageService.uploadFile(bucketName,"test",upload);
		String filename = upload.getOriginalFilename();
		UsersDTO dto = new UsersDTO();
		dto.setName(name);
		dto.setDescription(description);
		dto.setId((Integer) session.getAttribute("userId"));
		dto.setProfileImage(filename);
		usersService.updateUserById(dto);
		session.setAttribute("name",dto.getName());
		session.setAttribute("description",dto.getDescription());
		return new ResponseEntity<>(HttpStatus.OK);
	}
}

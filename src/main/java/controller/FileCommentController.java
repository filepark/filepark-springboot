package controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/group/{hashedId}/file/{fileId}/comment")
@RequiredArgsConstructor
public class FileCommentController {

}

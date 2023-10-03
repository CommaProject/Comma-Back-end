package com.team.comma.global.s3.controller;

import com.team.comma.global.message.MessageResponse;
import com.team.comma.global.s3.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping(value = "/file/upload", consumes = "multipart/form-data")
    public ResponseEntity<MessageResponse> uploadFile(
            @CookieValue String accessToken,
            @RequestParam MultipartFile image) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fileUploadService.uploadFileToS3(accessToken, image));
    }

}

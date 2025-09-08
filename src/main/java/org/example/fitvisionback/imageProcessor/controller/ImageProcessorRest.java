package org.example.fitvisionback.imageProcessor.controller;

import org.example.fitvisionback.imageProcessor.service.ImageProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("image-processor")
public class ImageProcessorRest {

    @Autowired
    private ImageProcessorService imageProcessorService;


    @PostMapping(
            value = "/generate-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public ResponseEntity<byte[]> generateImage(
            @RequestPart("baseImage") MultipartFile baseImage,
            @RequestPart("images") List<MultipartFile> images
    ) throws IOException {
        return ResponseEntity.ok().body(this.imageProcessorService.generateImage(baseImage, images));
    }

}

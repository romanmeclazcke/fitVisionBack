package org.example.fitvisionback.iaImageGenerator.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IaImageGeneratorService {
    byte [] generateImage(String prompt, MultipartFile baseImage, List<MultipartFile> clothes) throws IOException;
}

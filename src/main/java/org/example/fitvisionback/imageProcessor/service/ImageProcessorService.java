package org.example.fitvisionback.imageProcessor.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageProcessorService {
    byte[] generateImage(MultipartFile baseImage, List<MultipartFile> images) throws IOException;
}

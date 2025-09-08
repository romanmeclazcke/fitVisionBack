package org.example.fitvisionback.imageProcessor.service;

import org.example.fitvisionback.iaImageGenerator.service.IaImageGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ImageProcessorServiceImpl implements ImageProcessorService {


    @Autowired
    private IaImageGeneratorService iaImageGenerator;

    @Override
    public byte[] generateImage(MultipartFile baseImage, List<MultipartFile> clothes) throws IOException {
        String prompt= "Use image 1 (the person) as the base subject." +
                "Overlay clothing from the other images onto the person realistically." +
                "Keep the original background of image 1 unchanged." +
                "Respect pose, perspective, shadows, wrinkles, and natural occlusions (arms, hair)." +
                "Preserve the person’s facial identity." +
                "Output a realistic PNG at ~1024x1024 resolution.";
        return this.iaImageGenerator.generateImage(prompt, baseImage, clothes);
    }
}

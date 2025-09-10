package org.example.fitvisionback.imageProcessor.service;

import lombok.RequiredArgsConstructor;
import org.example.fitvisionback.credits.service.CreditsService;
import org.example.fitvisionback.exceptions.InsufficientCreditsException;
import org.example.fitvisionback.iaImageGenerator.service.IaImageGeneratorService;
import org.example.fitvisionback.user.entity.User;
import org.example.fitvisionback.utils.GetUserConected;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ImageProcessorServiceImpl implements ImageProcessorService {



    private IaImageGeneratorService iaImageGenerator;
    private CreditsService creditsService;
    private GetUserConected getUserConected;

    @Autowired
    public ImageProcessorServiceImpl(IaImageGeneratorService iaImageGenerator, CreditsService creditsService, GetUserConected getUserConected) {
        this.iaImageGenerator = iaImageGenerator;
        this.creditsService = creditsService;
        this.getUserConected = getUserConected;
    }

    @Override
    public byte[] generateImage(MultipartFile baseImage, List<MultipartFile> clothes) throws IOException {
        User userConected = this.getUserConected.getUserConected();

        boolean userHasCredits = this.creditsService.userHasCredits(userConected); // Check if user has credits

        if (!userHasCredits){
            throw new InsufficientCreditsException();
        }

        String prompt = this.getPrompt();
        byte[] imageGenerated =this.iaImageGenerator.generateImage(prompt, baseImage, clothes);
        this.creditsService.useCredit(userConected); // Deduct a credit
        return imageGenerated;
    }

    private String getPrompt(){
        return "Use image 1 (the person) as the base subject." +
                "Overlay clothing from the other images onto the person realistically." +
                "Keep the original background of image 1 unchanged." +
                "Respect pose, perspective, shadows, wrinkles, and natural occlusions (arms, hair)." +
                "Preserve the person’s facial identity." +
                "Output a realistic PNG at ~1024x1024 resolution.";
    }
}

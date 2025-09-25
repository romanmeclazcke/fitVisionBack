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

    private String getPrompt() {

        return "Replace the person's clothing in the input image with the target clothing shown in the reference image. Keep the person's pose, facial expression, background, and overall realism unchanged. Make the new outfit look natural, well-fitted, and consistent with lighting and shadows. Do not alter the person's identity or the environment — only change the clothes.";
//        return
//                "Use image 1 as the base subject (the person).\n" +
//                        "Keep the original background, lighting, and reflections of image 1 completely unchanged.\n" +
//                        "(person's identity:1.3), (body shape:1.3), (pose:1.3), (natural lighting:1.2), (skin texture:1.2)\n" +
//                        "Overlay the clothing from image 2 onto the person with photorealistic precision:\n" +
//                        "- (match body proportions:1.3)\n" +
//                        "- (preserve pose:1.3)\n" +
//                        "- (realistic folds and wrinkles:1.2)\n" +
//                        "- (respect occlusions:1.2) caused by arms, hands, hair, or objects in image 1.\n" +
//                        "- (maintain original shadows and highlights:1.2)\n" +
//                        "- (warp clothing naturally to fit anatomy:1.3)\n" +
//                        "Blend edges smoothly so the clothing looks physically worn, not pasted.\n" +
//                        "Generate a high-resolution PNG (~1024x1024) with clean transparency if applicable.\n" +
//                        "Final output must look like an unedited real photo of the person wearing the clothing.\n\n" +
//
//                        "Negative prompt: (different person:1.5), (face replacement:1.5), (fake mannequin:1.5), " +
//                        "(distorted body:1.4), (extra limbs:1.4), (floating clothes:1.4), (cartoon:1.3), (ai-looking:1.3), " +
//                        "(unrealistic lighting:1.3), (blurry:1.3), (low quality:1.3)";
    }
}

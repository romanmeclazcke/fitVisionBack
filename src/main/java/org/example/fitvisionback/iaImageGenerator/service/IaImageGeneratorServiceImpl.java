package org.example.fitvisionback.iaImageGenerator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.fitvisionback.iaImageGenerator.service.IaImageGeneratorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class IaImageGeneratorServiceImpl implements IaImageGeneratorService {

    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String MODEL_NAME = "gemini-2.5-flash-image-preview";
    private static final String BASE_URL   = "https://generativelanguage.googleapis.com/v1beta";

    // Constructor correcto (mismo nombre que la clase). No hace falta @Autowired en Boot 3.x.
    public IaImageGeneratorServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public byte[] generateImage(String prompt, MultipartFile baseImage, List<MultipartFile> clothes) throws IOException {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Falta configurar la api key");
        }
        if (baseImage == null || baseImage.isEmpty()) {
            throw new IllegalArgumentException("La imagen base (persona) es obligatoria");
        }

        String promptToUse = generatePrompt(prompt);


        List<Object> parts = new ArrayList<>();
        parts.add(Map.of("text", promptToUse));
        parts.add(inlineDataPart(baseImage));
        if (clothes != null) {
            for (MultipartFile c : clothes) {
                if (c != null && !c.isEmpty()) {
                    parts.add(inlineDataPart(c));
                }
            }
        }

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", parts
                ))
        );

        String json = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/{model}:generateContent")
                        .queryParam("key", apiKey)
                        .build(MODEL_NAME))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (json == null || json.isBlank()) {
            throw new IllegalStateException("Respuesta vacía de Gemini");
        }

        String b64 = extractFirstInlineImageBase64(json);
        if (b64 == null || b64.isBlank()) {
            throw new IllegalStateException("No se encontró imagen en la respuesta de Gemini: " + json);
        }
        return Base64.getDecoder().decode(b64);
    }

    private Map<String, Object> inlineDataPart(MultipartFile file) throws IOException {
        String mime = (file.getContentType() == null || file.getContentType().isBlank())
                ? "image/png" : file.getContentType();
        String data64 = Base64.getEncoder().encodeToString(file.getBytes());
        return Map.of("inlineData", Map.of(
                "mimeType", mime,
                "data", data64
        ));
    }

    private String extractFirstInlineImageBase64(String json) throws IOException {
        JsonNode root = mapper.readTree(json);
        JsonNode parts = root.path("candidates").path(0).path("content").path("parts");
        if (!parts.isArray()) return null;
        for (JsonNode p : parts) {
            if (p.has("inlineData") && p.get("inlineData").has("data")) {
                return p.get("inlineData").get("data").asText();
            }
        }
        return null;
    }

    private String generatePrompt(String prompt){
        return Objects.isNull(prompt) || prompt.isBlank()
                ? "Use image 1 (the person) as the base subject." +
                "Overlay clothing from the other images onto the person realistically." +
                "Keep the original background of image 1 unchanged." +
                "Respect pose, perspective, shadows, wrinkles, and natural occlusions (arms, hair)." +
                "Preserve the person’s facial identity." +
                "Output a realistic PNG at ~1024x1024 resolution."
                : prompt;

    }
}
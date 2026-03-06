package org.example.fitvisionback.iaImageGenerator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class IaImageGeneratorServiceImpl implements IaImageGeneratorService {

    private final WebClient webClient;
    private final ObjectMapper mapper;
    private final String modelName;

    @Value("${gemini.api.key}")
    private String apiKey;

    public IaImageGeneratorServiceImpl(
            WebClient.Builder webClientBuilder,
            @Value("${gemini.api.base-url:https://generativelanguage.googleapis.com/v1beta}") String baseUrl,
            @Value("${gemini.api.model:gemini-2.0-flash-exp-image-generation}") String modelName

    ) {
        this.modelName = modelName;
        this.mapper = new ObjectMapper();
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        log.info("Gemini client init | baseUrl={} | model={}", baseUrl, modelName);
    }

    @Override
    public byte[] generateImage(String prompt, MultipartFile baseImage, List<MultipartFile> clothes) throws IOException {
        log.info("Starting Gemini image generation | model={} | baseImage={} | clothes={} | promptPreview={}", modelName, fileInfo(baseImage), clothesInfo(clothes), preview(prompt));
        System.out.println("Starting Gemini image generation | model=" + modelName + " | baseImage=" + fileInfo(baseImage) + " | clothes=" + clothesInfo(clothes) + " | promptPreview=" + preview(prompt));
        validateInput(baseImage);

        List<Object> parts = new ArrayList<>();
        parts.add(Map.of("text", generatePrompt(prompt)));
        parts.add(inlineDataPart(baseImage));

        if (clothes != null) {
            for (MultipartFile cloth : clothes) {
                if (cloth != null && !cloth.isEmpty()) {
                    parts.add(inlineDataPart(cloth));
                }
            }
        }

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", parts
                        )
                ),
                "generationConfig", Map.of(
                        "responseModalities", List.of("TEXT", "IMAGE")
                )
        );

        log.info("Gemini request | model={} | baseImage={} | clothes={} | promptPreview={}", modelName, fileInfo(baseImage), clothesInfo(clothes), preview(prompt));

        String jsonResponse = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/{model}:generateContent")
                        .queryParam("key", apiKey)
                        .build(modelName))
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        response -> logAndMapError(response)
                )
                .bodyToMono(String.class)
                .block();

        if (jsonResponse == null || jsonResponse.isBlank()) {
            throw new IllegalStateException("Respuesta vacía de Gemini");
        }

        log.info("Gemini raw response (trimmed) | {}", jsonResponse.length() > 500 ? jsonResponse.substring(0, 500) + "..." : jsonResponse);

        String base64Image = extractFirstInlineImageBase64(jsonResponse);

        if (base64Image == null || base64Image.isBlank()) {
            throw new IllegalStateException("No se encontró imagen en la respuesta de Gemini: " + jsonResponse);
        }

        return Base64.getDecoder().decode(base64Image);
    }

    private void validateInput(MultipartFile baseImage) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Falta configurar la api key de Gemini");
        }

        if (baseImage == null || baseImage.isEmpty()) {
            throw new IllegalArgumentException("La imagen base (persona) es obligatoria");
        }
    }

    private Map<String, Object> inlineDataPart(MultipartFile file) throws IOException {
        String mimeType = (file.getContentType() == null || file.getContentType().isBlank())
                ? "image/png"
                : file.getContentType();

        String dataBase64 = Base64.getEncoder().encodeToString(file.getBytes());

        log.info("Embedding file | name={} | size={}B | mime={}", file.getOriginalFilename(), file.getSize(), mimeType);

        return Map.of(
                "inlineData", Map.of(
                        "mimeType", mimeType,
                        "data", dataBase64
                )
        );
    }

    private String extractFirstInlineImageBase64(String json) throws IOException {
        JsonNode root = mapper.readTree(json);
        JsonNode candidates = root.path("candidates");

        if (!candidates.isArray() || candidates.isEmpty()) {
            return null;
        }

        for (JsonNode candidate : candidates) {
            JsonNode parts = candidate.path("content").path("parts");

            if (!parts.isArray()) {
                continue;
            }

            for (JsonNode part : parts) {
                JsonNode inlineData = part.path("inlineData");
                String data = inlineData.path("data").asText(null);

                if (data != null && !data.isBlank()) {
                    return data;
                }
            }
        }

        return null;
    }

    private String generatePrompt(String prompt) {
        if (Objects.nonNull(prompt) && !prompt.isBlank()) {
            return prompt;
        }

        return """
            You are a virtual try-on AI.
            
            Image 1 is a person. Image 2 (and any additional images) are clothing items.
            
            Your task:
            1. Keep the person's face, body, pose, and background EXACTLY as in Image 1.
            2. REPLACE the clothing the person is currently wearing with the clothing shown in Image 2.
            3. Make the new clothing look realistic: adjust for body shape, pose, lighting, shadows, and wrinkles.
            4. Do NOT describe the result. Do NOT add any text. Output ONLY the final image.
            5. The output must be a photorealistic image, not a drawing or illustration.
            """;
    }

    private Mono<? extends Throwable> logAndMapError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .map(body -> {
                    log.error("Gemini error | status={} | body={}", response.statusCode(), body);
                    return new IllegalStateException("Error de Gemini: " + body);
                });
    }

    private String fileInfo(MultipartFile file) {
        if (file == null) {
            return "null";
        }
        return String.format("name=%s size=%dB type=%s", file.getOriginalFilename(), file.getSize(), file.getContentType());
    }

    private String clothesInfo(List<MultipartFile> clothes) {
        if (clothes == null) {
            return "null";
        }
        return String.format("count=%d", clothes.stream().filter(Objects::nonNull).count());
    }

    private String preview(String prompt) {
        if (prompt == null) {
            return "null";
        }
        return prompt.length() <= 80 ? prompt : prompt.substring(0, 80) + "...";
    }
}

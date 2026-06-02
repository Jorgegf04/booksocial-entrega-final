package com.example.booksocial_frontend.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import com.example.booksocial_frontend.security.SessionJwtInterceptor;

import jakarta.annotation.PostConstruct;

/**
 * Servicio de file upload del frontend Thymeleaf.
 * @Service hace que Spring cree esta clase como componente para poder inyectarla.
 * Usa del backend: ruta configurada en el servicio.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Service
public class FileUploadClientService {

    /** @Value lee la URL base del backend desde application.properties o usa un valor por defecto. */
    @Value("${api.base-url:http://localhost:9999/api}")
    private String apiBaseUrl;

    /** @Autowired inyecta automaticamente el interceptor o dependencia que necesita el servicio. */
    @Autowired
    private SessionJwtInterceptor jwtInterceptor;

    private RestClient restClient;

    /** @PostConstruct ejecuta init despues de crear el servicio para preparar el RestClient. */
    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
            .baseUrl(Objects.requireNonNull(apiBaseUrl))
            .requestInterceptor(jwtInterceptor)
            .build();
    }

    /**
     * Sube el archivo al backend y devuelve la ruta generada.
     * Usa RestClient o datos de sesion para devolver la informacion al controlador.
     *
     * @author Jorge
     * @version 3
     * @since 01/06/2026
     */
    public String uploadImage(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "image";

            ByteArrayResource resource = new ByteArrayResource(bytes) {
                @Override
                public String getFilename() { return filename; }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);

            return restClient.post()
                .uri("/upload")
                .contentType(Objects.requireNonNull(MediaType.MULTIPART_FORM_DATA))
                .body(body)
                .retrieve()
                .body(String.class);

        } catch (Exception e) {
            throw new RuntimeException("Error al subir la imagen: " + e.getMessage(), e);
        }
    }
}

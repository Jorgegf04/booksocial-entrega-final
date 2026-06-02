package com.example.booksocial_backend.service;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

/**
 * Servicio de infraestructura para almacenamiento de archivos subidos.
 *
 * Inicializa el directorio local de subida y guarda cada archivo con un nombre
 * unico generado con {@link UUID}. Devuelve una ruta publica bajo
 * {@code /api/uploads} para que los frontends puedan mostrar el recurso.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Service
public class FileStorageService {

    /** Ruta local absoluta donde se escriben los archivos subidos. */
    private Path uploadDir;

    /** Directorio base configurable para almacenar los archivos en disco. */
    @Value("${upload.dir:uploads}")
    private String uploadDirPath;

    /** URL publica base que se antepone a las rutas expuestas por la API. */
    @Value("${upload.base-url:}")
    private String baseUrl;

  /**
   * Inicializa los recursos necesarios para el servicio.
   *
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
    */
    @PostConstruct
    public void init() {
        // Normaliza la ruta para evitar diferencias entre rutas relativas y absolutas.
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de uploads: " + uploadDir, e);
        }
    }

  /**
   * Almacena fisicamente un archivo recibido por la API.
   *
   * @param file archivo recibido desde la peticion HTTP
   * @return ruta publica del archivo almacenado
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
   */
    public String store(MultipartFile file) {
        String rawName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String original = StringUtils.cleanPath(rawName);
        String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
        // Genera un nombre unico para evitar colisiones y no exponer el nombre original.
        String filename = UUID.randomUUID() + ext;

        try {
            Files.copy(file.getInputStream(), uploadDir.resolve(filename),
                StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo", e);
        }

        String normalizedBaseUrl = baseUrl == null ? "" : baseUrl.trim();
        // Normaliza la URL base para evitar dobles barras al construir la ruta publica.
        if (normalizedBaseUrl.endsWith("/")) {
            normalizedBaseUrl = normalizedBaseUrl.substring(0, normalizedBaseUrl.length() - 1);
        }

        return normalizedBaseUrl + "/api/uploads/" + filename;
    }
}

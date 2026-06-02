package com.example.booksocial_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.booksocial_backend.service.FileStorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Gestiona la subida de archivos al backend de BookSocial.
 * Recibe ficheros enviados desde el cliente, valida que tengan contenido y delega su almacenamiento.
 * Devuelve la ruta o referencia generada por el servicio de ficheros.
 *
 * @author Jorge
 * @version 2
 * @since 2026-05-12
 */
@Tag(name = "FileUploadController", description = "API REST para subida y almacenamiento de archivos")
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

  private final FileStorageService fileStorageService;

  /**
   * Recibe un archivo enviado por el cliente, valida que no esté vacío y devuelve la referencia almacenada.
   * Coordina la petición REST con el servicio correspondiente y devuelve una respuesta HTTP adecuada.
   *
   * @author Jorge
   * @version 2
   * @since 2026-05-12
   */
  @Operation(summary = "Subir archivo", description = "Recibe un archivo enviado por el cliente, valida que no esté vacío y devuelve la referencia almacenada.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Archivo subido correctamente"),
      @ApiResponse(responseCode = "400", description = "Archivo vacio o solicitud invalida"),
      @ApiResponse(responseCode = "401", description = "No autenticado"),
      @ApiResponse(responseCode = "403", description = "No autorizado"),
      @ApiResponse(responseCode = "500", description = "Error al almacenar el archivo")
  })
  @PostMapping
  public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
    if (file.isEmpty()) {
      return ResponseEntity.badRequest().body("El archivo está vacío");
    }
    return ResponseEntity.ok(fileStorageService.store(file));
  }
}

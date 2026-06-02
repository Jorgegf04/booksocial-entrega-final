package com.example.booksocial_backend.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

// Codigo de la ilustración 40
/**
 * Manejador global de excepciones para la API REST de BookSocial.
 *
 * Intercepta excepciones de dominio, validacion, base de datos y errores no
 * controlados para devolver siempre un {@link ExceptionBody} con codigo HTTP
 * consistente y mensaje legible para el cliente.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // =========================
  // USER
  // =========================

  /**
   * Gestiona errores de usuario no encontrado y devuelve HTTP 404.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ExceptionBody> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
    log.warn("[USER] [READ] [NOT_FOUND] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  /**
   * Gestiona conflictos por usuarios duplicados y devuelve HTTP 409.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ExceptionBody> handleUserExists(UserAlreadyExistsException ex, HttpServletRequest request) {
    log.warn("[USER] [CREATE] [CONFLICT] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
  }

  // =========================
  // AUTHOR
  // =========================

  /**
   * Gestiona errores de autor no encontrado y devuelve HTTP 404.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(AuthorNotFoundException.class)
  public ResponseEntity<ExceptionBody> handleAuthorNotFound(AuthorNotFoundException ex, HttpServletRequest request) {
    log.warn("[AUTHOR] [READ] [NOT_FOUND] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  /**
   * Gestiona conflictos por autores duplicados y devuelve HTTP 409.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(AuthorAlreadyExistsException.class)
  public ResponseEntity<ExceptionBody> handleAuthorExists(AuthorAlreadyExistsException ex, HttpServletRequest request) {
    log.warn("[AUTHOR] [CREATE] [CONFLICT] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
  }

  // =========================
  // WORK
  // =========================

  /**
   * Gestiona errores de obra no encontrada y devuelve HTTP 404.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(WorkNotFoundException.class)
  public ResponseEntity<ExceptionBody> handleWorkNotFound(WorkNotFoundException ex, HttpServletRequest request) {
    log.warn("[WORK] [READ] [NOT_FOUND] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  // =========================
  // EDITION / EDITORIAL / VOLUME / PRODUCT
  // =========================

  /**
   * Gestiona errores de edicion no encontrada y devuelve HTTP 404.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(EditionNotFoundException.class)
  public ResponseEntity<ExceptionBody> handleEditionNotFound(EditionNotFoundException ex, HttpServletRequest request) {
    log.warn("[EDITION] [READ] [NOT_FOUND] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  /**
   * Gestiona errores de editorial no encontrada y devuelve HTTP 404.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(EditorialNotFoundException.class)
  public ResponseEntity<ExceptionBody> handleEditorialNotFound(EditorialNotFoundException ex,
      HttpServletRequest request) {
    log.warn("[EDITORIAL] [READ] [NOT_FOUND] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  /**
   * Gestiona errores de volumen no encontrado y devuelve HTTP 404.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(VolumeNotFoundException.class)
  public ResponseEntity<ExceptionBody> handleVolumeNotFound(VolumeNotFoundException ex, HttpServletRequest request) {
    log.warn("[VOLUME] [READ] [NOT_FOUND] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  /**
   * Gestiona errores de producto no encontrado y devuelve HTTP 404.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<ExceptionBody> handleProductNotFound(ProductNotFoundException ex, HttpServletRequest request) {
    log.warn("[PRODUCT] [READ] [NOT_FOUND] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  // =========================
  // ORDER / ORDER LINE
  // =========================

  /**
   * Gestiona errores de pedido no encontrado y devuelve HTTP 404.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(OrderNotFoundException.class)
  public ResponseEntity<ExceptionBody> handleOrderNotFound(OrderNotFoundException ex, HttpServletRequest request) {
    log.warn("[ORDER] [READ] [NOT_FOUND] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  /**
   * Gestiona errores de linea de pedido no encontrada y devuelve HTTP 404.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(OrderLineNotFoundException.class)
  public ResponseEntity<ExceptionBody> handleOrderLineNotFound(OrderLineNotFoundException ex,
      HttpServletRequest request) {
    log.warn("[ORDER_LINE] [READ] [NOT_FOUND] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  // =========================
  // COMMENT / REACTION
  // =========================

  /**
   * Gestiona errores de comentario no encontrado y devuelve HTTP 404.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(CommentNotFoundException.class)
  public ResponseEntity<ExceptionBody> handleCommentNotFound(CommentNotFoundException ex, HttpServletRequest request) {
    log.warn("[COMMENT] [READ] [NOT_FOUND] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  /**
   * Gestiona errores de reaccion no encontrada y devuelve HTTP 404.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(ReactionNotFoundException.class)
  public ResponseEntity<ExceptionBody> handleReactionNotFound(ReactionNotFoundException ex,
      HttpServletRequest request) {
    log.warn("[REACTION] [READ] [NOT_FOUND] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  // =========================
  // SUBSCRIPTION / EVENT / TRACKING
  // =========================

  /**
   * Gestiona errores de suscripcion no encontrada y devuelve HTTP 404.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(SubscriptionNotFoundException.class)
  public ResponseEntity<ExceptionBody> handleSubscriptionNotFound(SubscriptionNotFoundException ex,
      HttpServletRequest request) {
    log.warn("[SUBSCRIPTION] [READ] [NOT_FOUND] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  /**
   * Gestiona conflictos por suscripciones ya activas y devuelve HTTP 409.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(SubscriptionAlreadyActiveException.class)
  public ResponseEntity<ExceptionBody> handleSubscriptionAlreadyActive(SubscriptionAlreadyActiveException ex,
      HttpServletRequest request) {
    log.warn("[SUBSCRIPTION] [CREATE] [CONFLICT] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
  }

  /**
   * Gestiona errores de evento no encontrado y devuelve HTTP 404.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(EventNotFoundException.class)
  public ResponseEntity<ExceptionBody> handleEventNotFound(EventNotFoundException ex, HttpServletRequest request) {
    log.warn("[EVENT] [READ] [NOT_FOUND] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  /**
   * Gestiona errores de seguimiento de obra no encontrado y devuelve HTTP 404.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(TrackingWorkNotFoundException.class)
  public ResponseEntity<ExceptionBody> handleTrackingWorkNotFound(TrackingWorkNotFoundException ex,
      HttpServletRequest request) {
    log.warn("[TRACKING_WORK] [READ] [NOT_FOUND] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  /**
   * Gestiona conflictos por seguimientos de obra duplicados y devuelve HTTP 409.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(TrackingWorkAlreadyExistsException.class)
  public ResponseEntity<ExceptionBody> handleTrackingWorkExists(TrackingWorkAlreadyExistsException ex,
      HttpServletRequest request) {
    log.warn("[TRACKING_WORK] [CREATE] [CONFLICT] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
  }

  /**
   * Gestiona errores de seguimiento de pedido no encontrado y devuelve HTTP 404.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(TrackingOrderNotFoundException.class)
  public ResponseEntity<ExceptionBody> handleTrackingOrderNotFound(TrackingOrderNotFoundException ex,
      HttpServletRequest request) {
    log.warn("[TRACKING_ORDER] [READ] [NOT_FOUND] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  // =========================
  // EDITORIAL (EXTENDED)
  // =========================

  /**
   * Gestiona conflictos por editoriales duplicadas y devuelve HTTP 409.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(EditorialAlreadyExistsException.class)
  public ResponseEntity<ExceptionBody> handleEditorialExists(EditorialAlreadyExistsException ex,
      HttpServletRequest request) {
    log.warn("[EDITORIAL] [CREATE] [CONFLICT] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
  }

  /**
   * Gestiona conflictos al eliminar editoriales con ediciones asociadas y
   * devuelve HTTP 409.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(EditorialHasEditionsException.class)
  public ResponseEntity<ExceptionBody> handleEditorialHasEditions(EditorialHasEditionsException ex,
      HttpServletRequest request) {
    log.warn("[EDITORIAL] [DELETE] [CONFLICT] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
  }

  // =========================
  // EDITION (EXTENDED)
  // =========================

  /**
   * Gestiona conflictos por ediciones duplicadas y devuelve HTTP 409.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(EditionAlreadyExistsException.class)
  public ResponseEntity<ExceptionBody> handleEditionExists(EditionAlreadyExistsException ex,
      HttpServletRequest request) {
    log.warn("[EDITION] [CREATE] [CONFLICT] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
  }

  /**
   * Gestiona conflictos al eliminar ediciones con lineas de pedido asociadas y
   * devuelve HTTP 409.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(EditionHasOrderLinesException.class)
  public ResponseEntity<ExceptionBody> handleEditionHasOrderLines(EditionHasOrderLinesException ex,
      HttpServletRequest request) {
    log.warn("[EDITION] [DELETE] [CONFLICT] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
  }

  // =========================
  // TOME (EXTENDED)
  // =========================

  /**
   * Gestiona conflictos por tomos duplicados y devuelve HTTP 409.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(TomeAlreadyExistsException.class)
  public ResponseEntity<ExceptionBody> handleTomeExists(TomeAlreadyExistsException ex, HttpServletRequest request) {
    log.warn("[TOME] [CREATE] [CONFLICT] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
  }

  // =========================
  // CHAPTER / TOME
  // =========================

  /**
   * Gestiona errores de capitulo no encontrado y devuelve HTTP 404.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(ChapterNotFoundException.class)
  public ResponseEntity<ExceptionBody> handleChapterNotFound(ChapterNotFoundException ex, HttpServletRequest request) {
    log.warn("[CHAPTER] [READ] [NOT_FOUND] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  /**
   * Gestiona conflictos por capitulos duplicados y devuelve HTTP 409.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(ChapterAlreadyExistsException.class)
  public ResponseEntity<ExceptionBody> handleChapterExists(ChapterAlreadyExistsException ex,
      HttpServletRequest request) {
    log.warn("[CHAPTER] [CREATE] [CONFLICT] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
  }

  /**
   * Gestiona errores de tomo no encontrado y devuelve HTTP 404.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(TomeNotFoundException.class)
  public ResponseEntity<ExceptionBody> handleTomeNotFound(TomeNotFoundException ex, HttpServletRequest request) {
    log.warn("[TOME] [READ] [NOT_FOUND] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
  }

  // =========================
  // VALIDATION
  // =========================

  /**
   * Gestiona validaciones de negocio y devuelve HTTP 400.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ExceptionBody> handleValidation(ValidationException ex, HttpServletRequest request) {
    log.warn("[VALIDATION] [ERROR] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
  }

  /**
   * Gestiona errores de validacion de Bean Validation y devuelve detalle por
   * campo con HTTP 400.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionBody> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpServletRequest request) {
    List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> Map.of("field", fe.getField(), "message",
            fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Valor inválido"))
        .toList();
    log.warn("[VALIDATION] [BEAN] [ERROR] {} campo(s) inválido(s): {}", fieldErrors.size(), fieldErrors);
    return buildResponseWithFields("Errores de validación en la petición", HttpStatus.BAD_REQUEST, request,
        fieldErrors);
  }

  /**
   * Gestiona violaciones de restricciones en parametros y devuelve HTTP 400.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ExceptionBody> handleConstraintViolation(ConstraintViolationException ex,
      HttpServletRequest request) {
    String message = ex.getConstraintViolations().stream()
        .findFirst()
        .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
        .orElse("Restricción de validación violada");
    log.warn("[VALIDATION] [CONSTRAINT] [ERROR] {}", message);
    return buildResponse(message, HttpStatus.BAD_REQUEST, request);
  }

  // =========================
  // JSON ERROR
  // =========================

  /**
   * Gestiona cuerpos JSON mal formados o no legibles y devuelve HTTP 400.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
  public ResponseEntity<ExceptionBody> handleJsonError(Exception ex, HttpServletRequest request) {
    log.warn("[REQUEST] [PARSE] [ERROR] JSON mal formado: {}", ex.getMessage());
    return buildResponse("El cuerpo de la petición tiene formato inválido", HttpStatus.BAD_REQUEST, request);
  }

  // =========================
  // INTEGRIDAD DE DATOS (FK, UNIQUE, NOT NULL en BD)
  // DEBE ir ANTES que DataAccessException porque es subclase
  // =========================

  /**
   * Gestiona restricciones de integridad de base de datos y devuelve HTTP 409.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ExceptionBody> handleDataIntegrity(DataIntegrityViolationException ex,
      HttpServletRequest request) {
    Throwable root = ex.getRootCause();
    String rootMsg = (root != null) ? root.getMessage() : ex.getMessage();
    log.warn("[DB] [INTEGRITY] [CONFLICT] {}", rootMsg);

    String userMessage;
    if (rootMsg != null && rootMsg.toLowerCase().contains("duplicate")) {
      userMessage = "Ya existe un registro con esos datos (clave duplicada)";
    } else if (rootMsg != null && rootMsg.toLowerCase().contains("foreign key")) {
      userMessage = "No se puede eliminar el registro porque tiene datos relacionados (pedidos, comentarios u otras referencias)";
    } else if (rootMsg != null && rootMsg.toLowerCase().contains("cannot be null")) {
      userMessage = "Faltan campos obligatorios en la base de datos: " + rootMsg;
    } else {
      userMessage = "Operación no permitida por restricción de integridad de datos";
    }

    return buildResponse(userMessage, HttpStatus.CONFLICT, request);
  }

  // =========================
  // BASE DE DATOS
  // =========================

  /**
   * Gestiona errores de conexion con la base de datos y devuelve HTTP 503.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(CannotCreateTransactionException.class)
  public ResponseEntity<ExceptionBody> handleCannotCreateTransaction(CannotCreateTransactionException ex,
      HttpServletRequest request) {
    log.error("[DB] [CONNECTION] [ERROR] No se puede conectar con la base de datos: {}", ex.getMessage());
    return buildResponse("No se puede conectar con la base de datos. Verifique que MySQL está activo.",
        HttpStatus.SERVICE_UNAVAILABLE, request);
  }

  /**
   * Gestiona errores generales de acceso a datos y devuelve HTTP 503.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<ExceptionBody> handleDataAccess(DataAccessException ex, HttpServletRequest request) {
    log.error("[DB] [ACCESS] [ERROR] Error de acceso a datos en {}: {}", request.getRequestURI(), ex.getMessage());
    return buildResponse("Error de acceso a datos. Por favor inténtelo más tarde.",
        HttpStatus.SERVICE_UNAVAILABLE, request);
  }

  // =========================
  // STOCK / AUTORIZACIÓN
  // =========================

  /**
   * Gestiona pedidos con stock insuficiente y devuelve HTTP 422.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(InsufficientStockException.class)
  public ResponseEntity<ExceptionBody> handleInsufficientStock(InsufficientStockException ex,
      HttpServletRequest request) {
    log.warn("[ORDER] [STOCK] [UNPROCESSABLE] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY, request);
  }

  /**
   * Gestiona acciones no autorizadas de negocio y devuelve HTTP 403.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(UnauthorizedActionException.class)
  public ResponseEntity<ExceptionBody> handleUnauthorizedAction(UnauthorizedActionException ex,
      HttpServletRequest request) {
    log.warn("[SECURITY] [UNAUTHORIZED] [FORBIDDEN] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.FORBIDDEN, request);
  }

  // =========================
  // ILLEGAL ARGUMENT
  // =========================

  /**
   * Gestiona argumentos invalidos y devuelve HTTP 400.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ExceptionBody> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
    log.warn("[REQUEST] [VALIDATION] [ERROR] {}", ex.getMessage());
    return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
  }

  // =========================
  // GENERIC
  // =========================

  /**
   * Gestiona cualquier excepcion no contemplada y devuelve HTTP 500.
   *
   * @param ex      excepcion capturada por Spring
   * @param request peticion HTTP que provoco el error
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionBody> handleGeneric(Exception ex, HttpServletRequest request) {
    log.error("[SYSTEM] [UNHANDLED] [ERROR] {} en {}: {}", ex.getClass().getSimpleName(), request.getRequestURI(),
        ex.getMessage(), ex);
    return buildResponse("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  // =========================
  // BUILDERS
  // =========================

  /**
   * Construye una respuesta de error estandar sin detalle de campos.
   *
   * @param message mensaje que se incluira en la respuesta
   * @param status  estado HTTP que se devolvera al cliente
   * @param request peticion HTTP original
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  private ResponseEntity<ExceptionBody> buildResponse(String message, HttpStatus status, HttpServletRequest request) {
    return ResponseEntity.status(status.value()).body(
        new ExceptionBody(
            LocalDateTime.now(),
            status.value(),
            message,
            request.getRequestURI()));
  }

  /**
   * Construye una respuesta de error estandar con detalle de errores por campo.
   *
   * @param message     mensaje que se incluira en la respuesta
   * @param status      estado HTTP que se devolvera al cliente
   * @param request     peticion HTTP original
   * @param fieldErrors errores de validacion asociados a campos concretos
   * @return respuesta HTTP con el cuerpo de error normalizado
   */
  private ResponseEntity<ExceptionBody> buildResponseWithFields(String message, HttpStatus status,
      HttpServletRequest request, List<Map<String, String>> fieldErrors) {
    return ResponseEntity.status(status.value()).body(
        new ExceptionBody(
            LocalDateTime.now(),
            status.value(),
            message,
            request.getRequestURI(),
            fieldErrors));
  }
}

package com.example.booksocial_frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import com.example.booksocial_frontend.dto.JwtResponseDTO;
import com.example.booksocial_frontend.dto.LoginRequestDTO;
import com.example.booksocial_frontend.dto.RegisterRequestDTO;
import com.example.booksocial_frontend.exception.ApiErrorUtils;
import com.example.booksocial_frontend.service.AuthClientService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

/**
 * Controlador de autenticacion del frontend. Muestra login y registro, conecta con el backend, guarda la sesion y elimina cookies.
 * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
 *
 * @author Jorge
 * @version 3
 * @since 01/06/2026
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

  private final AuthClientService authClientService;

  /**
   * Muestra la pantalla de login y prepara el formulario.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @GetMapping("/login")
  public String loginPage(Model model) {
    if (!model.containsAttribute("loginRequest")) {
      model.addAttribute("loginRequest", new LoginRequestDTO());
    }
    return "auth/login";
  }

  /**
   * Procesa el inicio de sesion y guarda datos en sesion y cookies.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @PostMapping("/login")
  public String login(
      @ModelAttribute LoginRequestDTO request,
      HttpSession session,
      HttpServletResponse response,
      Model model) {

    try {
      JwtResponseDTO jwt = authClientService.login(request);

      if (jwt == null || jwt.getToken() == null || jwt.getUserId() == null) {
        model.addAttribute("loginRequest", request);
        model.addAttribute("error", "Credenciales incorrectas. Verifica tu usuario y contraseña.");
        return "auth/login";
      }

      session.setAttribute("JWT", jwt.getToken());
      session.setAttribute("userId", jwt.getUserId());
      session.setAttribute("username", jwt.getUsername());
      session.setAttribute("role", jwt.getRole());

      // Persistir sesión en cookies durante 7 días para que survive a reinicios
      int maxAge = 7 * 24 * 60 * 60;
      addCookie(response, "bs_jwt", jwt.getToken(), maxAge, true);
      addCookie(response, "bs_uid", String.valueOf(jwt.getUserId()), maxAge, false);
      addCookie(response, "bs_usr", jwt.getUsername(), maxAge, false);
      addCookie(response, "bs_role", jwt.getRole(), maxAge, false);

      return "redirect:/catalog";

    } catch (RestClientResponseException e) {
      model.addAttribute("loginRequest", request);
      model.addAttribute("error", ApiErrorUtils.extractApiError(e));
      return "auth/login";
    } catch (ResourceAccessException e) {
      model.addAttribute("loginRequest", request);
      model.addAttribute("error", "El servidor no está disponible. Inténtelo más tarde.");
      return "auth/login";
    } catch (Exception e) {
      model.addAttribute("loginRequest", request);
      model.addAttribute("error", "Credenciales incorrectas. Verifica tu usuario y contraseña.");
      return "auth/login";
    }
  }

  /**
   * Cierra la sesion y borra las cookies de autenticacion.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @GetMapping("/logout")
  public String logout(HttpSession session, HttpServletResponse response) {
    session.invalidate();
    // Borrar las cookies de autenticación
    for (String name : new String[] { "bs_jwt", "bs_uid", "bs_usr", "bs_role" }) {
      addCookie(response, name, "", 0, name.equals("bs_jwt"));
    }
    return "redirect:/auth/login";
  }

  /**
   * Crea una cookie con nombre, valor, duracion y seguridad.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  private void addCookie(HttpServletResponse response, String name, String value,
      int maxAge, boolean httpOnly) {
    Cookie cookie = new Cookie(name, value);
    cookie.setMaxAge(maxAge);
    cookie.setPath("/");
    cookie.setHttpOnly(httpOnly);
    response.addCookie(cookie);
  }

  /**
   * Muestra la pantalla de registro y prepara el formulario.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @GetMapping("/register")
  public String registerPage(Model model) {
    model.addAttribute("registerRequest", new RegisterRequestDTO());
    return "auth/register";
  }

  /**
   * Procesa el registro y muestra errores si el backend los devuelve.
   * Usa anotaciones de Spring MVC para conectar rutas con vistas Thymeleaf o respuestas del frontend.
   *
   * @author Jorge
   * @version 3
   * @since 01/06/2026
   */
  @PostMapping("/register")
  public String register(
      @ModelAttribute RegisterRequestDTO request,
      RedirectAttributes ra,
      Model model) {

    try {
      var localErrors = validateRegisterRequest(request);
      if (!localErrors.isEmpty()) {
        model.addAttribute("registerRequest", request);
        model.addAttribute("error", "Revisa el formulario antes de crear la cuenta.");
        model.addAttribute("errorDetails", localErrors);
        return "auth/register";
      }

      authClientService.register(request);
      ra.addFlashAttribute("success", "¡Cuenta creada! Ya puedes iniciar sesión.");
      return "redirect:/auth/login";
    } catch (ResourceAccessException e) {
      model.addAttribute("registerRequest", request);
      model.addAttribute("error", "El servidor no está disponible. Inténtelo más tarde.");
      return "auth/register";
    } catch (Exception e) {
      model.addAttribute("registerRequest", request);
      model.addAttribute("error", ApiErrorUtils.extractApiErrorSummary(e, "No se pudo crear la cuenta."));
      model.addAttribute("errorDetails", ApiErrorUtils.extractApiErrorDetails(e));
      return "auth/register";
    }
  }

  private java.util.List<String> validateRegisterRequest(RegisterRequestDTO request) {
    java.util.List<String> errors = new java.util.ArrayList<>();

    if (isBlank(request.getUsername())) {
      errors.add("El nombre de usuario es obligatorio.");
    } else if (request.getUsername().trim().length() < 3) {
      errors.add("El nombre de usuario debe tener al menos 3 caracteres.");
    } else if (request.getUsername().contains(" ")) {
      errors.add("El nombre de usuario no puede contener espacios.");
    }

    if (isBlank(request.getEmail())) {
      errors.add("El correo electrónico es obligatorio.");
    } else if (!request.getEmail().contains("@")) {
      errors.add("Introduce un correo electrónico válido.");
    }

    if (isBlank(request.getPassword())) {
      errors.add("La contraseña es obligatoria.");
    } else if (request.getPassword().length() < 6) {
      errors.add("La contraseña debe tener al menos 6 caracteres.");
    } else if (request.getPassword().length() > 100) {
      errors.add("La contraseña no puede superar 100 caracteres.");
    }

    return errors;
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}

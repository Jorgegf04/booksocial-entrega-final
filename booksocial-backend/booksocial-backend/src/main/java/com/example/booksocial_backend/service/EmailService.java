package com.example.booksocial_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de infraestructura para envio de correos electronicos.
 *
 * Encapsula el uso de {@link JavaMailSender} para enviar notificaciones de
 * seguimiento y nuevas obras. Si el correo no esta configurado, omite el envio
 * para no bloquear el flujo principal de la aplicacion.
 *
 * @author Jorge
 * @version 2
 * @since 12/05/2026
 */
@Slf4j
@Service
public class EmailService {

    /** Componente de Spring encargado de construir y enviar correos salientes. */
    @Autowired(required = false)
    private JavaMailSender mailSender;

    /** Direccion remitente configurada para los correos enviados por BookSocial. */
    @Value("${spring.mail.username:}")
    private String fromEmail;

  /**
   * Envia una notificacion de correo electronico relacionada con la operacion.
   *
   * @param toEmail direccion de correo electronico destinataria
   * @param authorName nombre del autor usado en la notificacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
    */
    public void sendFollowConfirmation(String toEmail, String authorName) {
        // El envio de correo es opcional y no debe romper el seguimiento de autores.
        if (mailSender == null || fromEmail == null || fromEmail.isBlank()) return;
        try {
            // Las notificaciones actuales son texto plano, por eso se usa SimpleMailMessage.
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(toEmail);
            msg.setFrom(fromEmail);
            msg.setSubject("¡Ahora sigues a " + authorName + " en BookSocial!");
            msg.setText(
                "¡Hola!\n\n" +
                "Has empezado a seguir a " + authorName + " en BookSocial.\n" +
                "Te notificaremos por email cada vez que publique una nueva obra.\n\n" +
                "— El equipo de BookSocial"
            );
            mailSender.send(msg);
            log.info("Email de seguimiento enviado a {}", toEmail);
        } catch (Exception e) {
            log.warn("No se pudo enviar email de seguimiento a {}: {}", toEmail, e.getMessage());
        }
    }

  /**
   * Envia una notificacion de correo electronico relacionada con la operacion.
   *
   * @param toEmail direccion de correo electronico destinataria
   * @param authorName nombre del autor usado en la notificacion
   * @param workTitle titulo de la obra usado en la notificacion
   * @return no devuelve contenido
   *
   * @author Jorge
   * @version 2
   * @since 12/05/2026
    */
    public void sendNewWorkNotification(String toEmail, String authorName, String workTitle) {
        // El envio de correo es opcional y no debe bloquear la publicacion de obras.
        if (mailSender == null || fromEmail == null || fromEmail.isBlank()) return;
        try {
            // Las notificaciones actuales son texto plano, por eso se usa SimpleMailMessage.
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(toEmail);
            msg.setFrom(fromEmail);
            msg.setSubject("Nueva obra de " + authorName + " — BookSocial");
            msg.setText(
                "¡Hola!\n\n" +
                authorName + " acaba de publicar una nueva obra: \"" + workTitle + "\".\n" +
                "Entra en BookSocial para descubrirla.\n\n" +
                "— El equipo de BookSocial"
            );
            mailSender.send(msg);
            log.info("Notificación de nueva obra enviada a {}", toEmail);
        } catch (Exception e) {
            log.warn("No se pudo enviar notificación de nueva obra a {}: {}", toEmail, e.getMessage());
        }
    }
}

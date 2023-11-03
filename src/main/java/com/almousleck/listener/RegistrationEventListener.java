package com.almousleck.listener;

import com.almousleck.event.RegistrationEvent;
import com.almousleck.user_api.User;
import com.almousleck.user_api.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationEventListener implements ApplicationListener<RegistrationEvent> {
    private final UserService userService;
    private final JavaMailSender mailSender;
    private User theUser;
    @Override
    public void onApplicationEvent(RegistrationEvent event) {
        //TODO catch the newly register user
         theUser = event.getUser();
        //TODO create a verification token for user
        String verificationToken = UUID.randomUUID().toString();
        //TODO insert the verification token
        userService.saveUserVerifyToken(theUser, verificationToken);
        //TODO Build the verification url to be sent to the user
        String url = event.getApplicationUrl() + "/api/users/verifyEmail?token=" + verificationToken;
        //TODO Send email
        try {
            sendVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        log.info("Haga clic en el enlace para verificar su cuenta:" + //
                ": {}", url);
    }

    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Verificación Email";
        String senderName = "Servicio de Registro xxxxxxxxx";
        String mailContent = "<p> Estimado usuario, " + theUser.getEmail() + ", </p>" +
                "<p>Bienvenido al portal de registro de xxxxxxxxxxxxx.</p> " +
                "Por favor siga el siguiente enlace para continuar el proceso de registro.</p>"+
                "<a href=\"" +url+ "\">Verifica tu correo electrónico para activar su cuenta\n" + //
                        "</a><br><br>"+
                "<p> Este enlace expirará dentro de 15 minutos. <br><br> Atentamente, <br><br> xxxxxxxxxxxxxxxxxxxxxx";;
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("alexander.chale.leon@gmail.com", senderName);
        messageHelper.setTo(theUser.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}

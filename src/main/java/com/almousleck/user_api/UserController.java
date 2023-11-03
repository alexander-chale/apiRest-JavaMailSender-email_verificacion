package com.almousleck.user_api;

import com.almousleck.event.RegistrationEvent;
import com.almousleck.token.VerificationToken;
import com.almousleck.token.VerificationTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenRepository verificationTokenRepository;
    @GetMapping("/all")
    public ResponseEntity<List<User>> fetchUsers() {
        return ResponseEntity.ok(userService.fetchUsers());
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody RegistrationRequest registrationRequest,
                               final HttpServletRequest request) {
        User user = userService.registerUser(registrationRequest);
        // registration even
        publisher.publishEvent(new RegistrationEvent(user, applicationUrl(request)));
        return "Éxito! Por favor revise su correo electrónico para confirmar su cuenta.";
    }

    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String token) {
        VerificationToken verificationToken =
                verificationTokenRepository.findByToken(token);
        if (verificationToken.getUser().isEnabled()) {
            return "La cuenta ya ha sido verificada, inicie sesión en su cuenta";
        }
        String verificationResult = userService.validateToken(token);
        if (verificationResult.equalsIgnoreCase("valid")) {
            return "Cuenta verificada exitosamente. Inicie sesión en su cuenta.";
        }
        return "Lo sentimos! Se encontró un token no válido";
    }

    public String applicationUrl(HttpServletRequest request) {
        return "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
    }
}

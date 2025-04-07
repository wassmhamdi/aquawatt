package com.siga.camndaapp.service;

import com.siga.camndaapp.domain.Utilisateur;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


/**
 * @author MHAMDI Wassim 28/02/2025
 * SIGA'S Product
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {


    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;


    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.base-url}")
    private String getBaseUrl;


    public void sendActivationEmail(Utilisateur user) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        // Set email details
        helper.setTo(user.getEmail());
        helper.setSubject("Activate Your Account");
        helper.setFrom(fromEmail);

        // Prepare Thymeleaf context
        Context context = new Context();
        context.setVariable("name", user.getFirstName()+" "+user.getLastName());
        String linkActivation = getBaseUrl + "activate?token=" + user.getActivationKey();
        context.setVariable("activationLink", linkActivation);

        // Render HTML template
        String htmlContent = templateEngine.process("email/activationEmail", context);
        helper.setText(htmlContent, true); // true = isHTML

        // Send email
        javaMailSender.send(mimeMessage);
    }

    public void sendPasswordResetMail(Utilisateur user) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        // Set email details
        helper.setTo(user.getEmail());
        helper.setSubject("Reset Your Password");
        helper.setFrom(fromEmail);

        // Prepare Thymeleaf context
        Context context = new Context();
        context.setVariable("name", user.getFirstName()+" "+user.getLastName());
        String linkActivation = getBaseUrl + "activate?token=" + user.getResetKey();
        context.setVariable("activationLink", linkActivation);

        // Render HTML template
        String htmlContent = templateEngine.process("email/resetPassword", context);
        helper.setText(htmlContent, true); // true = isHTML

        // Send email
        javaMailSender.send(mimeMessage);
    }


}

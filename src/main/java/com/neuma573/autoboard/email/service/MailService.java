package com.neuma573.autoboard.email.service;

import com.neuma573.autoboard.email.model.dto.MailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {

    @Value("${app.mail.path}")
    private String htmlFilePath;

    @Value("${app.domain}")
    private String domain;

    private final JavaMailSender mailSender;

    public void sendVerifyEmail(MailRequest mailRequest) {
        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlContent = readHtmlFile(htmlFilePath);

            helper.setTo(mailRequest.getTo());
            helper.setSubject("가입신청 안내입니다.");
            helper.setText(replaceTokenInHtml(Objects.requireNonNull(htmlContent), mailRequest), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String readHtmlFile(String htmlFilePath) {
        try {
            ClassPathResource resource = new ClassPathResource(htmlFilePath);
            byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String replaceTokenInHtml(String htmlContent, MailRequest mailRequest) {
        return htmlContent
                .replace("{token}", mailRequest.getVerificationToken().getToken())
                .replace("{name}", mailRequest.getName())
                .replace("{domain}", domain);
    }
}

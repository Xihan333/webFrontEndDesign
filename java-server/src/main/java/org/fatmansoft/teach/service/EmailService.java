package org.fatmansoft.teach.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendTextEmail(){
        sendTextEmail("xqli@sdu.edu.cn", "xqlisdu.edu.cn","测试","设置密码为123456");
    }
    public void sendTextEmail(String from,String to, String subject, String msg) {
        log.info("Simple Email sending start");
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setFrom(from);
        simpleMessage.setSubject(subject);
        simpleMessage.setText(msg);
        simpleMessage.setTo(to);
        javaMailSender.send(simpleMessage);
        log.info("Simple Email sent");
    }

    public void sendEmailWithAttachment() {
        log.info("Sending email with attachment start");

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {

            // Set multipart mime message true
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,
                    true);

            mimeMessageHelper.setTo("santosh@example.com");
            mimeMessageHelper
                    .setSubject("Spring Boot=> Sending email with attachment");
            mimeMessageHelper.setText(
                    "Dear Santosh, I have sent you Websparrow.org new logo. PFA.");

            // Attach the attachment
            mimeMessageHelper.addAttachment("logo.png",
                    new ClassPathResource("logo-100.png"));

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            log.error("Exeception=>sendEmailWithAttachment ", e);
        }

        log.info("Email with attachment sent");
    }

    public void sendHTMLEmail() {
        log.info("HTML email sending start");
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {

            // Set multipart mime message true
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,
                    true);

            mimeMessageHelper.setTo("manish@example.com");
            mimeMessageHelper.setSubject("Spring Boot=> Sending HTML email");

            String html = "<h3>Dear Manish</h3></br>"
                    + "<p>Many many congratulation for joining "
                    + "<strong>Websparrow.org Team</strong>.</p>" + "</br></br>"
                    + "<p>You are entitled for <code>Rs.5000</code> "
                    + "as joning bonus.</p>";
            mimeMessageHelper.setText(html, true);

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            log.error("Exeception=>sendHTMLEmail ", e);
        }

        log.info("HTML email sent");

    }
}

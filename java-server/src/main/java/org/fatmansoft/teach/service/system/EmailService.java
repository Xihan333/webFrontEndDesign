package org.fatmansoft.teach.service.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory
            .getLogger(EmailService.class);
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    String fromEmail;

    public void sendMailMessage(Map<String,Object> data){
        logger.info("Simple Email sending start");
        String email = (String) data.get("email");
        Integer code = (Integer) data.get("code");
        String type = (String) data.get("type");
        SimpleMailMessage message = switch (type){
            case "register" ->
                    createMessage("欢迎注册我们的网站",
                            "您的邮件注册验证码为:"+code+",有效时间3分钟,为了保证您的安全,请勿向他人泄露验证码信息",email);
            case "reset" ->
                    createMessage("你的密码重置邮件",
                            "你好，您正在进行重置密码操作:验证码:"+code+",有效时间3分钟，如非本人操作，请无视",email);
            default -> null;
        };
        if(message==null){
            return;
        }
        javaMailSender.send(message);
        logger.info("Simple Email sent");
    }

    private SimpleMailMessage createMessage(String title,String content,String email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(fromEmail);
        return message;
    }



    public void sendTextEmail(String from,String to, String subject, String msg) {
        logger.info("Simple Email sending start");
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setFrom(from);
        simpleMessage.setSubject(subject);
        simpleMessage.setText(msg);
        simpleMessage.setTo(to);
        javaMailSender.send(simpleMessage);
        logger.info("Simple Email sent");
    }


    public void sendEmailWithAttachment() {
        logger.info("Sending email with attachment start");

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
            logger.error("Exeception=>sendEmailWithAttachment ", e);
        }

        logger.info("Email with attachment sent");
    }

    public void sendHTMLEmail() {
        logger.info("HTML email sending start");
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
            logger.error("Exeception=>sendHTMLEmail ", e);
        }

        logger.info("HTML email sent");

    }
}

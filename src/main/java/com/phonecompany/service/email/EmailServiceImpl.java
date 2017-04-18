package com.phonecompany.service.email;

import com.phonecompany.exception.MailSendException;
import com.phonecompany.service.interfaces.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@PropertySource("classpath:mail.properties")
public class EmailServiceImpl implements EmailService {

    private JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendMail(SimpleMailMessage mailMessage) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setText(mailMessage.getText(), true); // true = isHtml
            mimeMessageHelper.setTo(mailMessage.getTo());
            mimeMessageHelper.setSubject(mailMessage.getSubject());
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new MailSendException(e);
        }
    }
}

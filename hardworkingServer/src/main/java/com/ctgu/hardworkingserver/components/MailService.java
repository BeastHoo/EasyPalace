package com.ctgu.hardworkingserver.components;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailService {

    @Autowired
    JavaMailSender javaMailSender;

    public void sendMail(String from, String to, String cc, String subject, String content)
    {
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(content);
//        simpleMailMessage.setCc(cc);
        javaMailSender.send(simpleMailMessage);
    }
}

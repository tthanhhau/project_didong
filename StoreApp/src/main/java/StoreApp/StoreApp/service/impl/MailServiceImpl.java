package StoreApp.StoreApp.service.impl;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import StoreApp.StoreApp.model.Mail;
import StoreApp.StoreApp.service.MailService;


@Service
public class MailServiceImpl implements MailService
{
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Override
	public void sendEmail(Mail mail) 
	{
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setSubject(mail.getMailSubject());
			mimeMessageHelper.setFrom(new InternetAddress(mail.getMailFrom()));
			mimeMessageHelper.setTo(mail.getMailTo());
			mimeMessageHelper.setText(mail.getMailContent());
			javaMailSender.send(mimeMessageHelper.getMimeMessage());
		}
		catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}

package cz.gattserver.grass3.services.impl;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

import cz.gattserver.grass3.services.MailService;

@Service
public class MailServiceImpl implements MailService {

	private String grassMailAddress;
	private String grassMailPassword;
	private String grassNotificationAddress;

	@Override
	public void sendToAdmin(String subject, String body) {
		sendEmail(grassNotificationAddress, subject, body);
	}

	@Override
	public void sendEmail(String toEmail, String subject, String body) {
		try {

			body += "\n\n--\nZasláno systémem GRASS3";

			final String fromEmail = grassMailAddress;
			final String password = grassMailPassword;

			Properties props = new Properties();

			props.put("mail.smtp.timeout", "10000");
			props.put("mail.smtp.connectiontimeout", "10000");

			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "465");

			props.put("mail.smtp.ssl.enable", "true"); // !!!
			props.put("mail.smtp.ssl.trust", "*");

			Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(fromEmail, password);
				}
			});

			try {

				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(fromEmail));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
				message.setSubject(subject);
				message.setText(body);

				System.out.println("Sending");
				Transport.send(message);
				System.out.println("Done");

			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getGrassMailAddress() {
		return grassMailAddress;
	}

	public void setGrassMailAddress(String grassMailAddress) {
		this.grassMailAddress = grassMailAddress;
	}

	public String getGrassMailPassword() {
		return grassMailPassword;
	}

	public void setGrassMailPassword(String grassMailPassword) {
		this.grassMailPassword = grassMailPassword;
	}

	public String getGrassNotificationAddress() {
		return grassNotificationAddress;
	}

	public void setGrassNotificationAddress(String grassNotificationAddress) {
		this.grassNotificationAddress = grassNotificationAddress;
	}

}

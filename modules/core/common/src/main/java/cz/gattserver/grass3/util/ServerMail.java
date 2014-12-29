package cz.gattserver.grass3.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ServerMail {

	public static void sendToAdmin(String subject, String body) {
		sendEmail("gattakahynca@seznam.cz", subject, body);
	}

	public static void sendEmail(String toEmail, String subject, String body) {
		try {

			final String fromEmail = ServerMailCredentials.FROM_EMAIL;
			final String password = ServerMailCredentials.PASSWORD;

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
				message.setSubject("Testing Subject");
				message.setText("Test Mail");

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
}

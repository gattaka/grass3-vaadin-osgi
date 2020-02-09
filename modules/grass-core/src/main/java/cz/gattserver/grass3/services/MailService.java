package cz.gattserver.grass3.services;

public interface MailService {

	void sendToAdmin(String subject, String body);

	void sendEmail(String toEmail, String subject, String body);
}

package com.sample.mail.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.mail.exception.CustomException;
import com.sample.mail.model.Email;
import com.sample.mail.model.EmailResponseApi;

@Service
public class EmailService {
	private static final Logger log = LoggerFactory.getLogger(EmailService.class);

	public EmailResponseApi pushEmail(String jsonEmail, MultipartFile[] files) {
		log.info("****************pushEmail*******************");
		ObjectMapper objectMapper = new ObjectMapper();
		Email mailData = new Email();
		try {
			mailData = objectMapper.readValue(jsonEmail, Email.class);
			log.info("Parsed email data: {}", mailData);
		} catch (JsonProcessingException ex) {
			log.error("Fail to Process the Json Data", ex);
			throw new CustomException(100, "Fail to Process the Json Data" + ex);
		}

		List<MultipartFile> attachments = new ArrayList<>();
		if (files != null && files.length > 0) {
			for (MultipartFile file : files) {
				attachments.add(file);
			}
			log.info("Number of attachments: {}", attachments.size());
		}

		if (mailData.getSubject().isEmpty()) {
			log.warn("Subject is blank");
			throw new CustomException(101, "Subject should not be blank");
		}

		if (mailData.getToAddresses().isEmpty()) {
			log.warn("TO address is blank");
			throw new CustomException(102, "TO should not be blank");
		}

		String[] TorecipientList = mailData.getToAddresses().split(";");
		for (String recipient : TorecipientList) {
			if (!emailValidate(recipient)) {
				log.warn("Invalid TO recipient email: {}", recipient);
				throw new CustomException(103, "In TO recipient email " + recipient + " invalid");
			}
		}

		if (!mailData.getBccAddresses().isBlank()) {
			String[] BCCrecipientList = mailData.getBccAddresses().split(";");
			for (String recipient : BCCrecipientList) {
				if (!emailValidate(recipient)) {
					log.warn("Invalid BCC recipient email: {}", recipient);
					throw new CustomException(104, "In BCC recipient email " + recipient + " invalid");
				}
			}
		}

		if (!mailData.getCcAddresses().isBlank()) {
			String[] CCrecipientList = mailData.getCcAddresses().split(";");
			for (String recipient : CCrecipientList) {
				if (!emailValidate(recipient)) {
					log.warn("Invalid CC recipient email: {}", recipient);
					throw new CustomException(105, "In CC recipient email " + recipient + " invalid");
				}
			}
		}

		boolean isEmailSend = sendMail(mailData, attachments);
		List<Email> emailList = new ArrayList<>();

		if (isEmailSend) {
			log.info("Email sent successfully");
			emailList.add(mailData);
		} else {
			log.error("Failed to send email");
		}

		if (!emailList.isEmpty()) {
			return new EmailResponseApi(1, "SUCCESS", emailList);
		}
		return new EmailResponseApi(1, "FAIL", emailList);
	}

	public boolean sendMail(Email mailData, List<MultipartFile> attachments) {
		String host = "smtp.gmail.com";
		String port = "587";
		String username = "shilufjfdufdis050"; // Replace with your email username
		String password = "shifddfkhjdis050"; // Replace with your email password
		String displayEmail = "no-reply@yourdomain.com"; // Replace with your display email

		try {
			Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");

			Session session = Session.getInstance(properties, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			if (mailData.getOrganisation().isBlank()) {
				mailData.setOrganisation("no-reply");
			}

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(displayEmail, mailData.getOrganisation()));
			message.setReplyTo(InternetAddress.parse(mailData.getFromAddress())); // Optionally setting the "Reply-To"
																					// address

			// Set recipients
			addRecipients(message, Message.RecipientType.TO, mailData.getToAddresses());
			addRecipients(message, Message.RecipientType.CC, mailData.getCcAddresses());
			addRecipients(message, Message.RecipientType.BCC, mailData.getBccAddresses());

			message.setSubject(mailData.getSubject());

			// Create a multipart message for attachment
			Multipart multipart = new MimeMultipart();

			// Set the email body
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(mailData.getBody(), "text/html");
			multipart.addBodyPart(messageBodyPart);

			List<String> attachmentUrls = new ArrayList<>();
			// Add attachments
			if (attachments != null) {
				mailData.setFileAttached(true);
				log.info("Attachments present. Number of attachments: {}", attachments.size());

				for (MultipartFile multipartFile : attachments) {
					MimeBodyPart attachPart = new MimeBodyPart();
					try {
						File file = convertToFile(multipartFile);
						attachPart.attachFile(file);
						log.info("Attached file: {}", file.getName());
					} catch (Exception ex) {
						log.error("Error attaching file", ex);
					}
					multipart.addBodyPart(attachPart);
				}
			}

			// Send the complete message parts
			message.setContent(multipart);

			// Send message
			Transport.send(message);
			log.info("Email sent to SMTP server");

			return true;
		} catch (Exception e) {
			log.error("Error sending email", e);
		}
		return false;
	}

	private void addRecipients(Message message, Message.RecipientType type, String recipients) throws Exception {
		if (recipients != null && !recipients.trim().isEmpty()) {
			String[] recipientList = recipients.split(";");
			for (String recipient : recipientList) {
				message.addRecipient(type, new InternetAddress(recipient.trim()));
			}
			log.info("Added {} recipients to {}", recipientList.length, type);
		}
	}

	private File convertToFile(MultipartFile multipartFile) throws IOException {
		File file = new File(multipartFile.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(multipartFile.getBytes());
			log.info("Converted MultipartFile to File: {}", file.getName());
		}
		return file;
	}

	public static boolean emailValidate(String emailId) {
		Pattern pattern = null;
		Matcher matcher = null;
		try {
			pattern = Pattern
					.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
			matcher = pattern.matcher(emailId);
			return matcher.matches();
		} catch (Exception e) {
			log.error("Exception in emailValidate", e);
			return false;
		}
	}
}

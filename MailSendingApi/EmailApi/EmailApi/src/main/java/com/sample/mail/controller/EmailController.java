package com.sample.mail.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.mail.model.Email;
import com.sample.mail.model.EmailResponseApi;
import com.sample.mail.service.EmailService;

import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/emailApi")
public class EmailController{

	private static final Logger log = LoggerFactory.getLogger(EmailController.class);

	@Autowired
	private EmailService emailService;

	@GetMapping("/version")
	public String getVersion() {
		return "Email API v0.0.1 2nd June 2024";
	}

	@PostMapping("/sendEmail")
	public ResponseEntity<EmailResponseApi> sendEmail(@RequestPart(value = "email") String emailJson,
			@RequestPart(value = "attachments", required = false) MultipartFile[] files)
			throws JsonMappingException, JsonProcessingException {
		// Push email to service
		EmailResponseApi responseApi = emailService.pushEmail(emailJson, files);
		return new ResponseEntity<>(responseApi, HttpStatus.OK);
	}
	

	@GetMapping("/")
	public String googleAuth() {
		return "Welcome To google";
	}
	
	
}

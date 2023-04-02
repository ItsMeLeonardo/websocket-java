package com.app.web.controller;

import com.app.web.Model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
public class ChatController {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@MessageMapping("/message")
	@SendTo("/chatroom/public")
	public Message receiveMessage(@Payload Message message){
		return message;
	}

	@MessageMapping("/private-message")
	public Message recMessage(@Payload Message message){
		simpMessagingTemplate.convertAndSendToUser(message.to(),"/private",message);
		System.out.println(message.toString());

		return message;
	}
}
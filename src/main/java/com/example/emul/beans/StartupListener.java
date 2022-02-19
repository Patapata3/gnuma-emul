package com.example.emul.beans;

import com.example.emul.runnables.AliveThread;
import com.example.emul.util.FileUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StartupListener {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${classifier.name}")
    private String classifierName;
    @Value("${classifier.address}")
    private String address;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) throws IOException {
        String messageBody = FileUtils.getStringFromFile(String.format("static/startup_%s.json", classifierName));
        rabbitTemplate.convertAndSend("GNUMAExchange", "Classifier.distilbert", messageBody, message -> {
            message.getMessageProperties().setHeader("event", "ClassifierStart");
            return message;
        });
        new AliveThread("aliveThread", rabbitTemplate, address);
    }
}

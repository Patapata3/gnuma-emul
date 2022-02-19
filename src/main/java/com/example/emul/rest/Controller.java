package com.example.emul.rest;

import com.example.emul.beans.ThreadRepository;
import com.example.emul.dto.TrainRequestDTO;
import com.example.emul.runnables.TestThread;
import com.example.emul.runnables.TrainThread;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.UUID;

@RestController
public class Controller {
    @Autowired
    private ThreadRepository threadRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Value("${classifier.address}")
    private String address;

    @CrossOrigin
    @PostMapping("/train")
    public ResponseEntity<String> train(@RequestBody TrainRequestDTO requestBody) {
        UUID modelId = UUID.randomUUID();
        threadRepository.put(modelId, new TrainThread(rabbitTemplate, modelId, address));
        return ResponseEntity.ok(String.format("{\"model_id\": \"%s\"}", modelId));
    }

    @CrossOrigin
    @PutMapping("/pause/{id}")
    public ResponseEntity<String> pause(@PathVariable UUID id) {
        threadRepository.pause(id);
        return ResponseEntity.ok().build();
    }

    @CrossOrigin
    @DeleteMapping("/interrupt/{id}")
    public ResponseEntity<String> stop(@PathVariable UUID id) {
        threadRepository.stop(id);
        return ResponseEntity.ok().build();
    }

    @CrossOrigin
    @DeleteMapping("/models/{id}")
    public ResponseEntity<String> stopPaused(@PathVariable UUID id) {
        threadRepository.stop(id);
        return ResponseEntity.ok().build();
    }

    @CrossOrigin
    @PostMapping("/continue/{id}")
    public ResponseEntity<String> resume(@PathVariable UUID id) {
        threadRepository.resume(id);
        return ResponseEntity.ok().build();
    }

    @CrossOrigin
    @PostMapping("/evaluate/{id}")
    public ResponseEntity<String> test(@PathVariable UUID id, @RequestBody TrainRequestDTO body) {
        new TestThread(rabbitTemplate, id, address);
        return ResponseEntity.ok().build();
    }
}

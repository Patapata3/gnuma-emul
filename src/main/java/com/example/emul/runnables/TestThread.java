package com.example.emul.runnables;

import com.example.emul.dto.EvalFinishDTO;
import com.example.emul.dto.MetricDTO;
import com.google.gson.Gson;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.UUID;

public class TestThread implements Runnable {
    private RabbitTemplate rabbitTemplate;
    private Thread t;
    private UUID modelId;
    private String address;

    public TestThread(RabbitTemplate rabbitTemplate, UUID modelId, String address) {
        this.rabbitTemplate = rabbitTemplate;
        this.modelId = modelId;
        this.address = address;
        t = new Thread(this, modelId.toString());
        t.start();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
            return;
        }
        String messageBody = new Gson().toJson(new EvalFinishDTO(null, address, modelId, generateMetrics()));
        rabbitTemplate.convertAndSend("GNUMAExchange", "Classifier.distilbert", messageBody, message -> {
            message.getMessageProperties().setHeader("event", "EvaluationFinished");
            return message;
        });
    }

    private List<MetricDTO> generateMetrics() {
        return List.of(new MetricDTO("test_loss", Math.random() * 0.3 + 0.01), new MetricDTO("test_accuracy", Math.random() * 0.25 + 0.6));
    }
}

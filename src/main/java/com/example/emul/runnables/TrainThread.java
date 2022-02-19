package com.example.emul.runnables;

import com.example.emul.dto.ClassifierInterruptDTO;
import com.example.emul.dto.MetricDTO;
import com.example.emul.dto.TrainingUpdateDTO;
import com.google.gson.Gson;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.UUID;

public class TrainThread implements Runnable {
    private RabbitTemplate rabbitTemplate;
    private Thread t;
    private UUID modelId;
    private String address;
    private volatile boolean exit;
    private volatile boolean pause;
    private volatile boolean pausing;

    public TrainThread(RabbitTemplate rabbitTemplate, UUID modelId, String address) {
        this.rabbitTemplate = rabbitTemplate;
        this.modelId = modelId;
        this.address = address;
        t = new Thread(this, modelId.toString());
        exit = false;
        pause = false;
        t.start();
    }


    @Override
    public void run() {
        for (int i = 1; i <= 15; i++) {
            try {
                Thread.sleep(4000);

                while (pause && !exit) {
                    if (pausing) {
                        pausing = false;
                        sendInterruptMessage(true);
                    }
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            if (exit) {
                sendInterruptMessage(false);
                return;
            }
            String messageBody = new Gson()
                    .toJson(new TrainingUpdateDTO(modelId, address, i, 15, i==15, generateMetrics(i, 15)));
            rabbitTemplate.convertAndSend("GNUMAExchange", "Classifier.distilbert", messageBody, message -> {
                message.getMessageProperties().setHeader("event", "TrainingUpdate");
                return message;
            });
        }
    }

    private List<MetricDTO> generateMetrics(int currentStep, int totalSteps) {
        return List.of(
                new MetricDTO("loss", Math.abs(1 - (1.0 / totalSteps) * currentStep + (Math.random() * 0.2 - 0.1))),
                new MetricDTO("accuracy", Math.min(0.95, (1.0 / totalSteps) * currentStep + (Math.random() * 0.2 - 0.1)))
                );
    }

    public void stop() {
        exit = true;
    }

    public void pause() {
        pausing = true;
        pause = true;
    }

    private void sendInterruptMessage(boolean pause) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        String messageBody = new Gson().toJson(new ClassifierInterruptDTO(address, modelId, pause));
        rabbitTemplate.convertAndSend("GNUMAExchange", "Classifier.distilbert", messageBody, message -> {
            message.getMessageProperties().setHeader("event", "ClassifierInterrupt");
            return message;
        });
    }

    public void resume() {
        pause = false;
    }
}

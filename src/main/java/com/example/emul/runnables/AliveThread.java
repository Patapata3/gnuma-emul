package com.example.emul.runnables;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;

public class AliveThread implements Runnable {
    // to stop the thread
    private volatile boolean exit;
    private RabbitTemplate rabbitTemplate;

    private String name;
    Thread t;

    private String address;

    public AliveThread(String threadname, RabbitTemplate rabbitTemplate, String address)
    {
        this.rabbitTemplate = rabbitTemplate;
        this.address = address;
        name = threadname;
        t = new Thread(this, name);
        exit = false;
        t.start(); // Starting the thread
    }

    @Override
    public void run() {
        while (!exit) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            rabbitTemplate.convertAndSend("GNUMAExchange", "Classifier.distilbert", String.format("{\"address\": \"%s\"}", address), message -> {
                message.getMessageProperties().setHeader("event", "ClassifierAlive");
                return message;
            });
        }
    }

    public void stop() {
        exit = true;
    }
}

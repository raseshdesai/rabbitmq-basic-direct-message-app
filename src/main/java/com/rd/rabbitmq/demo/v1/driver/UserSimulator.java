package com.rd.rabbitmq.demo.v1.driver;


import com.rabbitmq.client.ConnectionFactory;
import com.rd.rabbitmq.demo.v1.manager.RabbitMqManager;
import com.rd.rabbitmq.demo.v1.manager.UserMessageManager;

import java.util.List;
import java.util.Random;

/**
 * Created by Rasesh Desai on 9/22/14.
 */
public class UserSimulator {

    public static void main(String[] args) {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        factory.setHost("localhost");
        factory.setPort(5672);

        RabbitMqManager rabbitMqManager = new RabbitMqManager(factory);
        rabbitMqManager.start();

        UserMessageManager userMessageManager = new UserMessageManager(rabbitMqManager);
        long userId = 3;
        long maxUserId = 10;
        userMessageManager.onApplicationStart();
        userMessageManager.onUserLogin(userId);
        System.out.printf("User login: %d%n", userId);

        while (!Thread.currentThread().isInterrupted()) {
            try {
                final List<String> messages = userMessageManager.fetchUserMessages(userId);
                if (messages != null) {
                    for (final String message : messages) {
                        System.out.printf("## User %d received: %s%n", userId, message);
                    }
                }
                final long addresseeUserId = 1 + new Random().nextInt((int) maxUserId);
                userMessageManager.sendUserMessage(addresseeUserId, "hello from " + userId);
                System.out.printf("-> Message sent to user %d%n", addresseeUserId);
            } catch (final Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000L);
            } catch (final InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

package com.rd.rabbitmq.demo.v1.manager;

/**
 * Created by Rasesh Desai on 9/27/14.
 */
public class RunnableImpl implements Runnable {

    private RabbitMqManager rabbitMqManager;

    public RunnableImpl(RabbitMqManager rabbitMqManager) {
        this.rabbitMqManager = rabbitMqManager;
    }

    @Override
    public void run() {
        rabbitMqManager.start();
    }
}

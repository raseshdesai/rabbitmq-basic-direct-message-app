package com.rd.rabbitmq.demo.v1.manager;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Rasesh Desai on 9/27/14.
 */
public class ShutdownListenerImpl implements ShutdownListener {

    private final Logger LOGGER = Logger.getLogger(getClass().getName());

    private RabbitMqManager rabbitMqManager;
    private ConnectionFactory factory;
    private Connection connection;

    public ShutdownListenerImpl(RabbitMqManager rabbitMqManager, ConnectionFactory factory, Connection connection) {
        this.rabbitMqManager = rabbitMqManager;
        this.factory = factory;
        this.connection = connection;
    }

    @Override
    public void shutdownCompleted(final ShutdownSignalException cause) {
        // reconnect only on unexpected errors
        if (!cause.isInitiatedByApplication()) {
            LOGGER.log(Level.SEVERE, "Lost connection to " + factory.getHost() + ":" + factory.getPort(),
                    cause);

            connection = null;
            rabbitMqManager.asyncWaitAndReconnect();
        }
    }
}

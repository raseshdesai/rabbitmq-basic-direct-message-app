package com.rd.rabbitmq.demo.v1.manager;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rd.rabbitmq.demo.v1.channeloperation.ChannelOperator;

import javax.inject.Named;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by Rasesh Desai on 9/21/14.
 */

@Named
public class RabbitMqManager {

    private final Logger LOGGER = Logger.getLogger(getClass().getName());

    private final ConnectionFactory factory;
    private final ScheduledExecutorService executor;
    private volatile Connection connection;

    public RabbitMqManager(final ConnectionFactory factory) {
        this.factory = factory;
        executor = Executors.newSingleThreadScheduledExecutor();
        connection = null;
    }

    public void start() {
        try {
            connection = factory.newConnection();
            connection.addShutdownListener(new ShutdownListenerImpl(this, factory, connection));
            LOGGER.info("Connected to " + factory.getHost() + ":" + factory.getPort());
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to " + factory.getHost() + ":" + factory.getPort(), e);
            asyncWaitAndReconnect();
        }
    }

    public void stop() {
        executor.shutdownNow();
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to close connection", e);
        } finally {
            connection = null;
        }
    }

    public void asyncWaitAndReconnect() {
        executor.schedule(new RunnableImpl(this), 15, TimeUnit.SECONDS);
    }

    /**
     *
     * Technically provides a place-holder for implementing class to implement "call" and "getDesc"
     *
     * Like a template-method-pattern
     *
     * RabbitMqManager.call() does the following
     *
     *      1. createChannel() out of the connection it manages
     *      2. Invokes the call() on implementing class by providing it channel (and returns what it gets), so it can do what it wants to with channel
     *      3. In case of exception, invoke the getDesc() on implementing class, to print a better error message
     *      4. Finally closeChannel()
     *
     * @param channelOperator
     * @param <T>
     * @return
     */
    public <T> T call(final ChannelOperator<T> channelOperator) {
        final Channel channel = createChannel();

        if (channel != null) {
            try {
                return channelOperator.call(channel);
            } catch (final Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to run: " + channelOperator.getDescription() + " on channel: "
                        + channel, e);
            } finally {
                closeChannel(channel);
            }
        }

        return null;
    }

    private Channel createChannel() {
        try {
            return connection == null ? null : connection.createChannel();
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to create channel", e);
            return null;
        }
    }

    private void closeChannel(final Channel channel) {
        // isOpen is not fully trustable!
        if ((channel == null) || (!channel.isOpen())) {
            return;
        }

        try {
            channel.close();
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to close channel: " + channel, e);
        }
    }
}

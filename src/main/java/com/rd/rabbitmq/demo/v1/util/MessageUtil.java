package com.rd.rabbitmq.demo.v1.util;

import com.rabbitmq.client.Channel;
import com.rd.rabbitmq.demo.v1.manager.UserMessageManager;

import java.io.IOException;
import java.util.Map;
import com.rabbitmq.client.AMQP.Queue.BindOk;

/**
 * Created by Rasesh Desai on 9/27/14.
 */
public class MessageUtil {

    public static String getUserInboxQueue(final long userId) {
        return "user-inbox." + userId;
    }

    public static BindOk declareUserMessageQueueAndBindToExchange(final String queue, final Channel channel) throws IOException {
        // survive a server restart
        final boolean durable = true;
        // can be consumed by another connection
        final boolean exclusive = false;
        // keep the queue
        final boolean autoDelete = false;
        // no special arguments
        final Map<String, Object> arguments = null;
        channel.queueDeclare(queue, durable, exclusive, autoDelete, arguments);

        // bind the addressee's queue to the direct exchange
        final String routingKey = queue;
        return channel.queueBind(queue, UserMessageManager.USER_INBOXES_EXCHANGE, routingKey);
    }
}

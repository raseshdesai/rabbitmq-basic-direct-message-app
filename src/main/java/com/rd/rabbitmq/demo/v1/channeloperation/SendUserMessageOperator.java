package com.rd.rabbitmq.demo.v1.channeloperation;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rd.rabbitmq.demo.v1.manager.UserMessageManager;
import com.rd.rabbitmq.demo.v1.util.MessageUtil;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Rasesh Desai on 9/27/14.
 */
public class SendUserMessageOperator implements ChannelOperator<String> {

    public static final String MESSAGE_CONTENT_TYPE = "application/vnd.ccm.pmsg.v1+json";
    public static final String MESSAGE_ENCODING = "UTF-8";

    private long userId;
    private String message;

    public SendUserMessageOperator(long userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    @Override
    public String getDescription() {
        return "Sending message to user: " + userId;
    }

    @Override
    public String call(Channel channel) throws IOException {
        final String queue = MessageUtil.getUserInboxQueue(userId);

        // it may not exist so declare it
        MessageUtil.declareUserMessageQueueAndBindToExchange(queue, channel);

        final String messageId = UUID.randomUUID().toString();

        final AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().contentType(MESSAGE_CONTENT_TYPE)
                .contentEncoding(MESSAGE_ENCODING)
                .messageId(messageId)
                .deliveryMode(2)
                .build();

        final String routingKey = queue;

        // publish the message to the direct exchange
        channel.basicPublish(UserMessageManager.USER_INBOXES_EXCHANGE, routingKey, props,
                message.getBytes(MESSAGE_ENCODING));

        return messageId;
    }
}

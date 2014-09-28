package com.rd.rabbitmq.demo.v1.channeloperation;

import com.rabbitmq.client.AMQP.Queue.BindOk;
import com.rabbitmq.client.Channel;
import com.rd.rabbitmq.demo.v1.manager.UserMessageManager;
import com.rd.rabbitmq.demo.v1.util.MessageUtil;

import java.io.IOException;

/**
 * Created by Rasesh Desai on 9/27/14.
 */
public class UserLoginOperator implements ChannelOperator<BindOk> {

    private long userId;
    private String queue;

    public UserLoginOperator(long userId) {
        this.userId = userId;
        this.queue = MessageUtil.getUserInboxQueue(userId);
    }

    @Override
    public String getDescription() {
        return "Declaring user queue: " + queue + ", binding it to exchange: " + UserMessageManager.USER_INBOXES_EXCHANGE;
    }

    @Override
    public BindOk call(Channel channel) throws IOException {
        return MessageUtil.declareUserMessageQueueAndBindToExchange(queue, channel);
    }
}

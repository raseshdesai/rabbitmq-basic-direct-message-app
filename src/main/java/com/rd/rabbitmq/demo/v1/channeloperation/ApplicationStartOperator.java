package com.rd.rabbitmq.demo.v1.channeloperation;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.Exchange.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rd.rabbitmq.demo.v1.manager.UserMessageManager;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Rasesh Desai on 9/27/14.
 */
public class ApplicationStartOperator implements ChannelOperator<DeclareOk> {

    @Override
    public String getDescription() {
        return "Declaring direct exchange: " + UserMessageManager.USER_INBOXES_EXCHANGE;
    }

    @Override
    public DeclareOk call(Channel channel) throws IOException {
        return declareExchange(channel);
    }

    private DeclareOk declareExchange(Channel channel) throws IOException {
        final String exchange = UserMessageManager.USER_INBOXES_EXCHANGE;
        final String type = "direct";
        // survive a server restart
        final boolean durable = true;
        // keep it even if not in user
        final boolean autoDelete = false;
        // no special arguments
        final Map<String, Object> arguments = null;

        return channel.exchangeDeclare(exchange, type, durable, autoDelete, arguments);
    }
}

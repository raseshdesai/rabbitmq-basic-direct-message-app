package com.rd.rabbitmq.demo.v1.channeloperation;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import com.rd.rabbitmq.demo.v1.util.MessageUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rasesh Desai on 9/27/14.
 */
public class FetchUserMessageOperator implements ChannelOperator<List<String>> {

    private long userId;

    public FetchUserMessageOperator(long userId) {
        this.userId = userId;
    }

    @Override
    public String getDescription() {
        return "Fetching messages for user: " + userId;
    }

    @Override
    public List<String> call(Channel channel) throws IOException {
        final List<String> messages = new ArrayList<String>();

        final String queue = MessageUtil.getUserInboxQueue(userId);
        final boolean autoAck = true;

        GetResponse getResponse;

        while ((getResponse = channel.basicGet(queue, autoAck)) != null) {
            final String contentEncoding = getResponse.getProps().getContentEncoding();
            messages.add(new String(getResponse.getBody(), contentEncoding));
        }

        return messages;
    }
}

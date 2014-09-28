package com.rd.rabbitmq.demo.v1.channeloperation;

import com.rabbitmq.client.Channel;

import java.io.IOException;

/**
 * Created by Rasesh Desai on 9/21/14.
 */
public interface ChannelOperator<T> {

    String getDescription();

    T call(Channel channel) throws IOException;
}

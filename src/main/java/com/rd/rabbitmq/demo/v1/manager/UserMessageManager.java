package com.rd.rabbitmq.demo.v1.manager;

import com.rd.rabbitmq.demo.v1.channeloperation.ApplicationStartOperator;
import com.rd.rabbitmq.demo.v1.channeloperation.FetchUserMessageOperator;
import com.rd.rabbitmq.demo.v1.channeloperation.SendUserMessageOperator;
import com.rd.rabbitmq.demo.v1.channeloperation.UserLoginOperator;
import com.rd.rabbitmq.demo.v1.manager.RabbitMqManager;

import javax.inject.Named;
import java.util.List;

/**
 * Created by Rasesh Desai on 9/22/14.
 */
@Named
public class UserMessageManager {

    public static final String USER_INBOXES_EXCHANGE = "user-inboxes";

    RabbitMqManager rabbitMqManager;

    public UserMessageManager(RabbitMqManager rabbitMqManager) {
        this.rabbitMqManager = rabbitMqManager;
    }

    public void onApplicationStart() {
        rabbitMqManager.call(new ApplicationStartOperator());
    }

    public void onUserLogin(final long userId) {
        rabbitMqManager.call(new UserLoginOperator(userId));
    }

    public String sendUserMessage(final long userId, final String message) {
        return rabbitMqManager.call(new SendUserMessageOperator(userId, message));
    }

    public List<String> fetchUserMessages(final long userId) {
        return rabbitMqManager.call(new FetchUserMessageOperator(userId));
    }

}

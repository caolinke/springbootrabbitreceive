package com.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitProducer {
    private static final String EXCHANGE_NAME = "exchange_demo111";
    private static final String ROUTINT_KEY = "routing_key111";
    private static final String QUEUE_NAME = "queue_demo111";
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);      //Rabbit 默认服务端端口号为5672，15672为管理端端口号
        factory.setUsername("guest");
        factory.setPassword("guest");
        Connection connection = factory.newConnection();    //创建连接
        Channel channel = connection.createChannel();       //创建信道
        //创建一个type是direct，持久化的、非自动删除的交换器
        channel.exchangeDeclare(EXCHANGE_NAME,"direct",true,false,null);
        //创建一个持久化。非排他的。非自动删除的队列
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        //将交换器与队列通过路由键绑定
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,ROUTINT_KEY);
        //发送一条持久化消息
        String message = "这是一个字符串的测试消息，嗯，就是先测试一下";
        channel.basicPublish(EXCHANGE_NAME,ROUTINT_KEY,MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
        //关闭资源
        channel.close();
        connection.close();
        System.out.println("执行完毕");
    }
}

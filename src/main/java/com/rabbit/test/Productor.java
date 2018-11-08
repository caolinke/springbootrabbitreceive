package com.rabbit.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ生产者
 */
public class Productor {
    //定义用户名
    private static final String USER_NAME = "guest";
    //定义密码
    private static final String PASSWORD = "guest";
    //定义主机和端口
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 5672;
    //定义一个交换机的名称
    private static final String EXCHANGE_NAME = "exchangeTest";
    //定义一个队列的名称
    private static final String QUEUE_NAME = "queueTest";
    //定义Routing_key
    private static final String ROUTING_KEY = "RoutingKeyTest";

    public static void main(String[] args) throws IOException, TimeoutException {
        //利用connectionFactory创建connection,ConnectionFactory定义连接的服务器
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setUsername(USER_NAME);
        factory.setPassword(PASSWORD);
        //创建一个连接，Connection连接用来创建一个Channel信道
        Connection connection = factory.newConnection();
        //创建信道，用来传递消息和接收消息
        Channel channel = connection.createChannel();
        /**
         * 声明创建交换器
         * EXCHANGE_NAME : 交换机的名称
         * type:direct : 交换器的类型，常用的有direct  fanout  topic
         * durable : true  设置是否持久化 ，true持久化，false 非持久化
         * autoDelete : false 设置是否自动删除，true表示自动删除
         * internal : true  设置是否是内置的，true、表示是内置的交换器，客户端无法直接发送消息到这个交换器中，
         *                  只能通过交换器路由到交换器这种方式
         * argument : 其他一些结构化参数
         *
         * 以上参数有的可以省略，如果省略则使用缺省参数值
         */
        channel.exchangeDeclare(EXCHANGE_NAME,"direct",true,false,false,null);
        /**
         * 声明创建一个队列
         * QUEUE_NAME ： 队列的名称
         * durable： true 设置是否持久化 ，true持久化，false 非持久化
         * exclusive ： false 设置是否排他 为true则设置队列为排他的，该队列仅对首次声明她的连接可见，
         *              并在连接断开时自动删除。
         * autoDelete : false 设置是否自动删除，true表示自动删除
         */
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        /**
         * 将交换器和队列绑定起来
         * QUEUE_NAME ： 队列名称
         * EXCHANGE_NAME ： 交换器名称
         * ROUTING_KEY ： 用来绑定队列和交换器的路由键
         */
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,ROUTING_KEY);
        /**
         * 发送消息
         * EXCHANGE_NAME : 交换器的名称
         * ROUTING_KEY : 路由键，交换器根据路由键将消息存储到相应的队列中
         * props ：消息的基本属性集
         * byte[] body : 消息体（payload），真正需要发送的消息
         */

        String str = "hello message";
        channel.basicPublish(EXCHANGE_NAME,ROUTING_KEY,MessageProperties.PERSISTENT_TEXT_PLAIN,str.getBytes());

        //消息发送完成，关闭资源
        channel.close();
        connection.close();

    }
}

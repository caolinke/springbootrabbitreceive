package com.rabbit.test;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Consumer {
    //定义队列名称
    private static final String QUEUE_NAME = "queueTest";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection connection = factory.newConnection(new Address[]{new Address("127.0.0.1",5672)});
        Channel channel = connection.createChannel();
        boolean autoAck = false;
        channel.basicQos(64);
        System.out.println("开始取数据");
        channel.basicConsume(QUEUE_NAME,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String routingKey = envelope.getRoutingKey();
                System.out.println(routingKey);
                String contentType = properties.getContentType();
                System.out.println(contentType);

                System.out.println("receiveMessage : " + new String(body));
                //此处加上下面代码可以执行出上面的打印语句，否则控制台没有输出任何数据
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long deliveryTag = envelope.getDeliveryTag();
                channel.basicAck(deliveryTag,false);
            }
        });
        System.out.println("数据操作完成");
        //这里需要加上下面这一句，否则会报异常
        //等待回调函数执行完毕后，关闭资源
        TimeUnit.SECONDS.sleep(5);
        channel.close();
        connection.close();
    }
}

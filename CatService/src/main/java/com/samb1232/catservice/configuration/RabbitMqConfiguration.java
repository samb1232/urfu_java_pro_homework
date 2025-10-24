package com.samb1232.catservice.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {

    @Value("${app.rabbitmq.add_cat_request_queue}")
    private String addCatRequestQueueName;

    @Value("${app.rabbitmq.get_my_cats_queue}")
    private String getMyCatsQueueName;

    @Value("${app.rabbitmq.my_cats_response_queue}")
    private String myCatsResponseQueueName;

    @Bean
    public Queue addCatRequestQueue() {
        return new Queue(addCatRequestQueueName, true);
    }

    @Bean
    public Queue getMyCatsQueue() {
        return new Queue(getMyCatsQueueName, true);
    }

    @Bean
    public Queue myCatsResponseQueue() {
        return new Queue(myCatsResponseQueueName, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

package com.rmq.example.subscriber.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message; // Importação para o objeto Message do Spring AMQP
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.MethodInvocationRecoverer;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import java.util.Arrays; // Para depuração

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.host}")
    private String hostName;

    @Value("${rabbitmq.userName}")
    private String userName;

    @Value("${rabbitmq.password}")
    private String password;

    @Value("${rabbitmq.port}")
    private int port;

    @Value("${rabbitmq.queueName}")
    private String queueName;

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(hostName);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost("/");
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            RetryOperationsInterceptor retryOperationsInterceptor) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAdviceChain(retryOperationsInterceptor);
        return factory;
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Queue createQueue(AmqpAdmin amqpAdmin) {
        Queue q = QueueBuilder.durable(queueName).build();
        amqpAdmin.declareQueue(q);
        return q;
    }

    @Bean
    public Queue boqQueue(AmqpAdmin amqpAdmin) {
        Queue q = QueueBuilder.durable("boq." + queueName).build();
        amqpAdmin.declareQueue(q);
        return q;
    }

    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        RepublishMessageRecoverer recoverer = new RepublishMessageRecoverer(rabbitTemplate);
        recoverer.setErrorRoutingKeyPrefix("boq.");
        return recoverer;
    }

    @Bean
    public MethodInvocationRecoverer<?> rabbitMessageInvocationRecoverer(MessageRecoverer messageRecoverer) {
        return (args, cause) -> {
            if (args != null && args.length > 0) {
                for (Object arg : args) {
                    if (arg instanceof Message) {
                        Message originalMessage = (Message) arg;
                        messageRecoverer.recover(originalMessage, cause);
                        return null;
                    }
                }
            }
            System.err.println("AVISO: O MethodInvocationRecoverer não conseguiu extrair o objeto Spring AMQP Message dos argumentos do método para recuperação. Argumentos: " + Arrays.toString(args));
            throw new RuntimeException("Falha na recuperação da mensagem: O objeto AMQP Message original não foi encontrado nos argumentos do método listener.", cause);
        };
    }

    @Bean
    public RetryOperationsInterceptor retryOperationsInterceptor(MethodInvocationRecoverer<?> rabbitMessageInvocationRecoverer) {
        RetryOperationsInterceptor interceptor = RetryInterceptorBuilder
                .stateless()
                .maxAttempts(3)
                .backOffOptions(2000, 1, 100000)
                .recoverer(rabbitMessageInvocationRecoverer)
                .build();
        return interceptor;
    }
}

package com.stackroute.orderservice.integrationtest;

import com.stackroute.orderservice.dto.AddressDto;
import com.stackroute.orderservice.dto.CartProductDto;
import com.stackroute.orderservice.dto.OrderDto;
import com.stackroute.orderservice.service.MessageProducerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@EmbeddedKafka
@SpringBootTest
@DirtiesContext
public class MessageProducerServiceEmbeddedKafkaITTests {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MessageProducerServiceEmbeddedKafkaITTests.class);

    private static String TOPIC = "orders";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private MessageProducerService producer;

    private KafkaMessageListenerContainer<String, OrderDto> container;

    private BlockingQueue<ConsumerRecord<String, OrderDto>> records;

    @BeforeEach
    public void setUp() {
        records = new LinkedBlockingQueue<>();
        Map<String, Object> consumerProperties =
                KafkaTestUtils.consumerProps("consumer", "false",
                        embeddedKafkaBroker);

        DefaultKafkaConsumerFactory<String, OrderDto> consumerFactory =
                new DefaultKafkaConsumerFactory<String, OrderDto>(
                        consumerProperties, new StringDeserializer(), new JsonDeserializer<>(OrderDto.class, false));

        ContainerProperties containerProperties =
                new ContainerProperties(TOPIC);
        container = new KafkaMessageListenerContainer<>(consumerFactory,
                containerProperties);
        container
                .setupMessageListener(new MessageListener<String, OrderDto>() {
                    @Override
                    public void onMessage(
                            ConsumerRecord<String, OrderDto> record) {
                        LOGGER.debug("test-listener received message='{}'",
                                record.toString());
                        records.add(record);
                    }
                });
        container.start();
        ContainerTestUtils.waitForAssignment(container,
                embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @AfterEach
    public void tearDown() {
        container.stop();
    }

    @Test
    public void givenOrderDtoWhenSentToKafkaTopicThenReceivedSuccessfully() throws InterruptedException {

        AddressDto addressDto = new AddressDto("100", "street", "area", "city", "state", 10101, "name", 9999911111l);
        List<CartProductDto> productsDto = List.of(new CartProductDto("1", "Carrot", 10, 10));
        OrderDto orderDto = new OrderDto("o1", "1", productsDto, LocalDate.now(), addressDto, addressDto, "creditcard", 100, "completed");

        producer.sendOrderMessage(orderDto);
        ConsumerRecord<String, OrderDto> receivedMessage =
                records.poll(10, TimeUnit.SECONDS);
        assertThat(receivedMessage).isNotNull();
        assertThat(receivedMessage.value()).isEqualTo(orderDto);
    }
}
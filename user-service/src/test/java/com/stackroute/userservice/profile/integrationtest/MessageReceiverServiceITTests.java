package com.stackroute.userservice.profile.integrationtest;

import com.stackroute.userservice.errorhandling.exception.CustomerNotFoundException;
import com.stackroute.userservice.profile.dto.AddressDto;
import com.stackroute.userservice.profile.dto.CartProductDto;
import com.stackroute.userservice.profile.dto.OrderDto;
import com.stackroute.userservice.profile.service.CustomerService;
import com.stackroute.userservice.profile.service.MessageReceiverService;
import org.apache.kafka.common.serialization.StringSerializer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@EmbeddedKafka
@ExtendWith(SpringExtension.class)
@DirtiesContext
@SpringBootTest
public class MessageReceiverServiceITTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiverServiceITTests.class);

    private static final String TOPIC = "orders";


    @Autowired
    private MessageReceiverService receiverService;

    private KafkaTemplate<String, OrderDto> kafkaTemplate;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    private OrderDto orderDto;

    @BeforeEach
    public void setUp() {

        Map<String, Object> producerProperties =
                KafkaTestUtils.producerProps(embeddedKafkaBroker);
        ProducerFactory<String, OrderDto> producerFactory =
                new DefaultKafkaProducerFactory<>(producerProperties, new StringSerializer(), new JsonSerializer<OrderDto>());

        kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setDefaultTopic(TOPIC);

        for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry
                .getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer,
                    embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }

    @Test

    public void givenOrderMessageWhenMessagePostedInTopicThenMessageReceived() throws CustomerNotFoundException, InterruptedException {
        AddressDto addressDto = new AddressDto("100", "street", "area", "city", "state", 10101, "name", 9999911111l);
        List<CartProductDto> productsDto = List.of(new CartProductDto("1", "Carrot", 10, 10));
        orderDto = new OrderDto("o1", "1", productsDto, LocalDate.now(), addressDto, addressDto, "creditcard", 100, "completed");
        kafkaTemplate.sendDefault(orderDto);
        receiverService.getLatch().await(5000, TimeUnit.MILLISECONDS);
        Assertions.assertThat(receiverService.getLatch().getCount()).isEqualTo(0);
    }
}
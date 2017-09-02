package com.coffeetechgaff.kafka;

import com.coffeetechgaff.enums.Operation;
import com.coffeetechgaff.model.EsMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by VivekSubedi on 8/25/17.
 */
public class KafkaMessageProducer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaMessageProducer.class);

    private ObjectMapper mapper = new ObjectMapper();

    private KafkaProducer<String, String> kafkaProducer = null;
    private String topic = null;

    public KafkaMessageProducer(){
        super();
    }

    public KafkaMessageProducer(String brokerList, String topic){
        kafkaProducer = KafkaConnection.getKafkaProducer(brokerList);
        this.topic = topic;
    }

    public void sendMessage(EsMetadata metadata, Operation operation) throws IOException{
        JSONObject jsonObject = new JSONObject(mapper.writeValueAsString(metadata));
        jsonObject.put("Operation", operation);
        ProducerRecord<String, String> pr = new ProducerRecord<>(topic, "key", jsonObject.toString());
        kafkaProducer.send(pr);
        logger.info("Successfully send the message to kafka");
    }

}

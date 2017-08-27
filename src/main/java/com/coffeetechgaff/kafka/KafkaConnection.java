package com.coffeetechgaff.kafka;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

/**
 * Created by VivekSubedi on 8/25/17.
 */
public class KafkaConnection {
    private static KafkaProducer<String, String> producerConnection;

    /**
     * Returns the KafkaProducer. It is static because KafkaProducer is thread
     * safe
     *
     * @param brokerList
     * @return
     */
    public static KafkaProducer<String, String> getKafkaProducer(String brokerList){

        if(producerConnection != null){
            return producerConnection;
        }

        Properties properties = new Properties();
        properties.put("bootstrap.servers", brokerList);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serializationStringySerializer");
        producerConnection = new KafkaProducer<>(properties);
        return producerConnection;
    }

    public static void setProducerConnection(KafkaProducer<String, String> producerConnection){
        KafkaConnection.producerConnection = producerConnection;
    }

    public static void producerClose(KafkaProducer<String, String> producer){
        producer.close();
    }

    /**
     * Returns the KafkaConsumer. It is not static because KafkaConsumer is not
     * thread safe
     *
     * @param brokerList
     * @return
     */
    public KafkaConsumer<String, String> getKafkaConsumer(String brokerList, String group){
        Properties properties = new Properties();
        properties.put("bootstrap.servers", brokerList);
        properties.put("group.id", group);
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("session.timeout.ms", "6000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        return new KafkaConsumer<>(properties);
    }

    public void consumerClose(KafkaConsumer<String, String> consumer){
        consumer.close();
    }

    /**
     * Checks to see if Kafka is up and running. Throws an exception if not.
     *
     * @return
     */
    public static boolean testConnection(KafkaConsumer<String, String> kafkaConsumer){

        // try to immediately get a message from the rule topic; if this does
        // not work an
        // exception will be thrown
        try{
            kafkaConsumer.listTopics();
            kafkaConsumer.close();
            return true;
        }catch(Exception e){
            kafkaConsumer.close();
            return false;
        }
    }
}

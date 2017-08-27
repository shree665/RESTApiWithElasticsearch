package com.coffeetechgaff.dao;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by VivekSubedi on 8/18/17.
 */
public class ElasticsearchClient {
    private static Client client;

    private ElasticsearchClient(){
        super();
    }

    /**
     * Initialized the Elasticsearch Client if the client is not null. If the
     * client is not null, that client will be used
     *
     * @param clustername
     *            -clusername
     * @param ipAddress
     *            -ES ip address
     * @param port
     *            -ES port
     * @return @Client
     * @throws UnknownHostException
     */
    @SuppressWarnings("resource")
	public static Client getElasticsearchClient(String clustername, String ipAddress, int port) throws UnknownHostException{

        if(client != null){
            return client;
        }

        Settings settings = Settings.builder().put("cluster.name", clustername).build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ipAddress), port));

        return client;
    }

    /**
     * Setting up the client if the user want to use his client
     *
     * @param client
     *            -User defined Elasticsearch client
     */
    public static void setClient(Client client){
        ElasticsearchClient.client = client;
    }

    public static void closeClient(){
        if(client != null){
            client.close();
        }
    }
}

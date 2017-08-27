package com.coffeetechgaff.model;

import com.coffeetechgaff.utils.RestApiWithEsUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by VivekSubedi on 8/25/17.
 */
public class ElasticsearchSource extends Source implements Serializable {
    private static final long serialVersionUID = 7599484724904621705L;

    private static final String TYPE = RestApiWithEsUtils.SOURCE_TYPE_ES;

    @JsonProperty(RestApiWithEsUtils.SOURCE_FIELD_HOSTNAME)
    private String hostname;

    @JsonProperty(RestApiWithEsUtils.SOURCE_FIELD_TRANSPORTPORT)
    private int transportPort;

    @JsonProperty(RestApiWithEsUtils.SOURCE_FIELD_RESTPORT)
    private int restPort;

    @JsonProperty(RestApiWithEsUtils.SOURCE_FIELD_CLUSTER)
    private String cluster;

    @JsonProperty(RestApiWithEsUtils.SOURCE_FIELD_INDEX)
    private String index;

    public ElasticsearchSource(String hostname, int transportPort, int restPort, String cluster, String index){
        this.hostname = hostname;
        this.transportPort = transportPort;
        this.restPort = restPort;
        this.cluster = cluster;
        this.index = index;
    }

    public ElasticsearchSource(){
        // Empty default constructor.
    }

    public String getHostname(){
        return hostname;
    }

    public void seHostname(String hostname){
        this.hostname = hostname;
    }

    public int getTransportPort(){
        return transportPort;
    }

    public void setTransportPort(int transportPort){
        this.transportPort = transportPort;
    }

    public int getRestPort(){
        return restPort;
    }

    public void setRestPort(int restPort){
        this.restPort = restPort;
    }

    public String getCluster(){
        return cluster;
    }

    public void setCluster(String cluster){
        this.cluster = cluster;
    }

    public String getIndex(){
        return index;
    }

    public void setIndex(String index){
        this.index = index;
    }

    @Override
    public int hashCode(){
        return new HashCodeBuilder().append(hostname).append(transportPort).append(restPort).append(cluster)
                .append(index).hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof ElasticsearchSource){
            ElasticsearchSource other = (ElasticsearchSource) obj;
            return new EqualsBuilder().append(hostname, other.getHostname())
                    .append(transportPort, other.getTransportPort()).append(restPort, other.getRestPort())
                    .append(cluster, other.getCluster()).append(index, other.getIndex()).isEquals();
        }

        return false;
    }

    @Override
    public Map<String, String> toMap(){
        Map<String, String> map = new HashMap<>();
        map.put(RestApiWithEsUtils.SOURCE_TYPE, TYPE);
        map.put(RestApiWithEsUtils.SOURCE_FIELD_HOSTNAME, hostname);
        map.put(RestApiWithEsUtils.SOURCE_FIELD_CLUSTER, cluster);
        map.put(RestApiWithEsUtils.SOURCE_FIELD_INDEX, index);
        if(transportPort != 0){
            map.put(RestApiWithEsUtils.SOURCE_FIELD_TRANSPORTPORT, Integer.toString(transportPort));
        }
        if(restPort != 0){
            map.put(RestApiWithEsUtils.SOURCE_FIELD_RESTPORT, Integer.toString(restPort));
        }
        return map;
    }

    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

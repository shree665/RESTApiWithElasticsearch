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
public class KafkaSource  extends Source implements Serializable {
    private static final long serialVersionUID = -7149078947211768923L;

    private static final String TYPE = RestApiWithEsUtils.SOURCE_TYPE_KAFKA;

    @JsonProperty(RestApiWithEsUtils.SOURCE_FIELD_TOPICNAME)
    private String topicName;

    @JsonProperty(RestApiWithEsUtils.SOURCE_FIELD_ZOOKEEPERS)
    private String zookeepers;

    public KafkaSource(String topicName, String zookeepers){
        this.topicName = topicName;
        this.zookeepers = zookeepers;
    }

    public KafkaSource(){
        // Empty default constructor.
    }

    public String getTopicName(){
        return topicName;
    }

    public void setTopicName(String topicName){
        this.topicName = topicName;
    }

    public String getZookeepers(){
        return zookeepers;
    }

    public void setZookeepers(String zookeepers){
        this.zookeepers = zookeepers;
    }

    @Override
    public int hashCode(){
        return new HashCodeBuilder().append(topicName).append(zookeepers).hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof KafkaSource){
            KafkaSource other = (KafkaSource) obj;
            return new EqualsBuilder().append(topicName, other.getTopicName())
                    .append(zookeepers, other.getZookeepers()).isEquals();
        }

        return false;
    }

    @Override
    public Map<String, String> toMap(){
        Map<String, String> map = new HashMap<>();
        map.put(RestApiWithEsUtils.SOURCE_TYPE, TYPE);
        map.put(RestApiWithEsUtils.SOURCE_FIELD_TOPICNAME, topicName);
        map.put(RestApiWithEsUtils.SOURCE_FIELD_ZOOKEEPERS, zookeepers);
        return map;
    }

    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }


}

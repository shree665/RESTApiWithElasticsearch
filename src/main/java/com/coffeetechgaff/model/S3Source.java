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
public class S3Source extends Source implements Serializable {
    private static final long serialVersionUID = 6568044826417118072L;

    private static final String TYPE = RestApiWithEsUtils.SOURCE_TYPE_S3;

    @JsonProperty(RestApiWithEsUtils.SOURCE_FIELD_URL)
    private String url;

    public S3Source(String url){
        this.url = url;
    }

    public S3Source(){
        // Empty default constructor.
    }

    public String getUrl(){
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    @Override
    public int hashCode(){
        return new HashCodeBuilder().append(url).hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof S3Source){
            S3Source other = (S3Source) obj;
            return new EqualsBuilder().append(url, other.getUrl()).isEquals();
        }

        return false;
    }

    @Override
    public Map<String, String> toMap(){
        Map<String, String> map = new HashMap<>();
        map.put(RestApiWithEsUtils.SOURCE_TYPE, TYPE);
        map.put(RestApiWithEsUtils.SOURCE_FIELD_URL, url);
        return map;
    }

    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

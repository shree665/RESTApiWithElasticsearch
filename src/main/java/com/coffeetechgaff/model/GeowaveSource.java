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
public class GeowaveSource extends Source implements Serializable {
    private static final long serialVersionUID = 7573596736243401221L;

    private static final String TYPE = RestApiWithEsUtils.SOURCE_TYPE_GEOWAVE;

    @JsonProperty(RestApiWithEsUtils.SOURCE_FIELD_ZOOKEEPERS)
    private String zookeepers;

    @JsonProperty(RestApiWithEsUtils.SOURCE_FIELD_ACCUMULOINSTANCENAME)
    private String accumuloInstanceName;

    @JsonProperty(RestApiWithEsUtils.SOURCE_FIELD_ACCUMULOTABLENAMESPACE)
    private String accumuloTableNamespace;

    @JsonProperty(RestApiWithEsUtils.SOURCE_FIELD_ACCUMULOTABLENAME)
    private String accumuloTableName;

    public GeowaveSource(String zookeepers, String accumuloInstanceName, String accumuloTableNamespace,
                         String accumuloTableName){
        this.zookeepers = zookeepers;
        this.accumuloInstanceName = accumuloInstanceName;
        this.accumuloTableNamespace = accumuloTableNamespace;
        this.accumuloTableName = accumuloTableName;
    }

    public GeowaveSource(){
        // Empty default constructor.
    }

    public String getZookeepers(){
        return zookeepers;
    }

    public void setZookeepers(String zookeepers){
        this.zookeepers = zookeepers;
    }

    public String getAccumuloInstanceName(){
        return accumuloInstanceName;
    }

    public void setAccumuloInstanceName(String accumuloInstanceName){
        this.accumuloInstanceName = accumuloInstanceName;
    }

    public String getAccumuloTableNamespace(){
        return accumuloTableNamespace;
    }

    public void setAccumuloTableNamespace(String accumuloTableNamespace){
        this.accumuloTableNamespace = accumuloTableNamespace;
    }

    public String getAccumuloTableName(){
        return accumuloTableName;
    }

    public void setAccumuloTableName(String accumuloTableName){
        this.accumuloTableName = accumuloTableName;
    }

    @Override
    public int hashCode(){
        return new HashCodeBuilder().append(zookeepers).append(accumuloInstanceName).append(accumuloTableNamespace)
                .append(accumuloTableName).hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof GeowaveSource){
            GeowaveSource other = (GeowaveSource) obj;
            return new EqualsBuilder().append(zookeepers, other.getZookeepers())
                    .append(accumuloInstanceName, other.getAccumuloInstanceName())
                    .append(accumuloTableNamespace, other.getAccumuloTableNamespace())
                    .append(accumuloTableName, other.getAccumuloTableName()).isEquals();
        }

        return false;
    }

    @Override
    public Map<String, String> toMap(){
        Map<String, String> map = new HashMap<>();
        map.put(RestApiWithEsUtils.SOURCE_TYPE, TYPE);
        map.put(RestApiWithEsUtils.SOURCE_FIELD_ZOOKEEPERS, zookeepers);
        map.put(RestApiWithEsUtils.SOURCE_FIELD_ACCUMULOINSTANCENAME, accumuloInstanceName);
        map.put(RestApiWithEsUtils.SOURCE_FIELD_ACCUMULOTABLENAMESPACE, accumuloTableNamespace);
        map.put(RestApiWithEsUtils.SOURCE_FIELD_ACCUMULOTABLENAME, accumuloTableName);
        return map;
    }

    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

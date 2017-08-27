package com.coffeetechgaff.model;

import com.coffeetechgaff.utils.RestApiWithEsUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.joda.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by VivekSubedi on 8/18/17.
 */
@ApiModel(value = "EsMetadata", description = "Model for metadata", subTypes = {S3Source.class}, discriminator = RestApiWithEsUtils.TYPE)
public class EsMetadata implements Serializable {

    private static final long serialVersionUID = -5218361146092116620L;

    @JsonProperty(RestApiWithEsUtils.SOURCE_FIELD_NAME)
    private String name;

    @JsonProperty(RestApiWithEsUtils.STATUS)
    private String status;

    @JsonProperty(RestApiWithEsUtils.SOURCE_FIELD_DESCRIPTION)
    private String description;

    @JsonProperty(RestApiWithEsUtils.SOURCE)
    private Source source;

    @JsonProperty(RestApiWithEsUtils.ID)
    private String id;

    @JsonProperty(RestApiWithEsUtils.SOURCE_FIELD_CREATION_DATE)
    private String creationDate;

    @JsonProperty(RestApiWithEsUtils.SOURCE_FIELD_LAST_UPDATED)
    private String lastUpdated;

    @JsonProperty(RestApiWithEsUtils.SOURCE_FIELD_EXPIRATION_DATE)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime expirationDate;


    @JsonProperty(RestApiWithEsUtils.SOURCE_FIELD_CLASSIFICATION)
    private String classification;

    @JsonProperty(RestApiWithEsUtils.SOURCE_FIELD_MATURITY)
    private String maturity;


    @ApiModelProperty(value = "Data source name", required = false)
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    @ApiModelProperty(value = "Data source description", required = false)
    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public Source getSource(){
        return source;
    }

    public void setSource(Source source){
        this.source = source;
    }

    @ApiModelProperty(value = "Id is set by Data API", required = false)
    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    @ApiModelProperty(value = "CreationDate is set by Data API", required = false)
    public String getCreationDate(){
        return creationDate;
    }

    public void setCreationDate(String creationDate){
        this.creationDate = creationDate;
    }

    @ApiModelProperty(value = "lastUpdated is set by Data API", required = false)
    public String getLastUpdated(){
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated){
        this.lastUpdated = lastUpdated;
    }

    @ApiModelProperty(value = "expirationDate tells the Data API manager when the data sources can be wiped", required = false)
    public LocalDateTime getExpirationDate(){
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate){
        this.expirationDate = expirationDate;
    }

    @ApiModelProperty(value = "status is set by Data API", required = false)
    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    @ApiModelProperty(value = "classification level", required = false)
    public String getClassification(){
        return classification;
    }

    public void setClassification(String classification){
        this.classification = classification;
    }

    @ApiModelProperty(value = "Maturity level of data", required = false, example = RestApiWithEsUtils.GOLD)
    public String getMaturity(){
        return maturity;
    }

    public void setMaturity(String maturity){
        this.maturity = maturity;
    }


    @Override
    public int hashCode(){
        return new HashCodeBuilder().append(name).append(description).append(classification).append(maturity)
                .append(source).append(id).append(creationDate).append(lastUpdated).append(expirationDate)
                .hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof EsMetadata){
            return EqualsBuilder.reflectionEquals(this, obj);
        }

        return false;
    }

    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

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
public class OGCSource extends Source implements Serializable {
    private static final long serialVersionUID = 7599597980056546385L;

    private static final String TYPE = RestApiWithEsUtils.SOURCE_TYPE_OGC;

    @JsonProperty(RestApiWithEsUtils.SOURCE_OGC_BASEURI)
    private String baseUri;

    @JsonProperty(RestApiWithEsUtils.SOURCE_OGC_WMSURI)
    private String wmsUri;

    @JsonProperty(RestApiWithEsUtils.SOURCE_OGC_WFSURI)
    private String wfsUri;

    @JsonProperty(RestApiWithEsUtils.SOURCE_OGC_GEOJSONURI)
    private String geojsonUri;

    @JsonProperty(RestApiWithEsUtils.SOURCE_OGC_STORE_NAME)
    private String storeName;

    @JsonProperty(RestApiWithEsUtils.SOURCE_OGC_STORE_TYPE)
    private String storeType;

    @JsonProperty(RestApiWithEsUtils.SOURCE_OGC_STORE_DATASOURCE_ID)
    private String storeDataSourceId;

    @JsonProperty(RestApiWithEsUtils.SOURCE_OGC_STORE_WORKSPACE)
    private String storeWorkspace;

    @JsonProperty(RestApiWithEsUtils.SOURCE_OGC_STORE_LAYERNAME)
    private String storeLayerName;

    public OGCSource(String baseUri, String wmsUri, String wfsUri, String geojsonUri, String storeName,
                     String storeType, String storeDataSourceId, String storeWorkspace, String storeLayerName){
        this.baseUri = baseUri;
        this.wmsUri = wmsUri;
        this.wfsUri = wfsUri;
        this.geojsonUri = geojsonUri;
        this.storeName = storeName;
        this.storeType = storeType;
        this.storeDataSourceId = storeDataSourceId;
        this.storeWorkspace = storeWorkspace;
        this.storeLayerName = storeLayerName;
    }

    public OGCSource(){
        // Empty default constructor.
    }

    public String getBaseUri(){
        return baseUri;
    }

    public void setBaseUri(String baseUri){
        this.baseUri = baseUri;
    }

    public String getWmsUri(){
        return wmsUri;
    }

    public void setWmsUri(String wmsUri){
        this.wmsUri = wmsUri;
    }

    public String getWfsUri(){
        return wfsUri;
    }

    public void setWfsUri(String wfsUri){
        this.wfsUri = wfsUri;
    }

    public String getGeojsonUri(){
        return geojsonUri;
    }

    public void setGeojsonUri(String geojsonUri){
        this.geojsonUri = geojsonUri;
    }

    public String getStoreName(){
        return storeName;
    }

    public void setStoreName(String storeName){
        this.storeName = storeName;
    }

    public String getStoreType(){
        return storeType;
    }

    public void setStoreType(String storeType){
        this.storeType = storeType;
    }

    public String getStoreDataSourceId(){
        return storeDataSourceId;
    }

    public void setStoreDataSourceId(String storeDataSourceId){
        this.storeDataSourceId = storeDataSourceId;
    }

    public String getStoreWorkspace(){
        return storeWorkspace;
    }

    public void setStoreWorkspace(String storeWorkspace){
        this.storeWorkspace = storeWorkspace;
    }

    public String getStoreLayerName(){
        return storeLayerName;
    }

    public void setStoreLayerName(String storeLayerName){
        this.storeLayerName = storeLayerName;
    }

    @Override
    public int hashCode(){
        return new HashCodeBuilder().append(baseUri).append(wmsUri).append(wfsUri).append(geojsonUri).append(storeName)
                .append(storeType).append(storeDataSourceId).append(storeWorkspace).append(storeLayerName).hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof OGCSource){
            OGCSource other = (OGCSource) obj;
            return new EqualsBuilder().append(baseUri, other.getBaseUri()).append(wmsUri, other.getWmsUri())
                    .append(wfsUri, other.getWfsUri()).append(geojsonUri, other.getGeojsonUri())
                    .append(storeName, other.getStoreName()).append(storeType, other.getStoreType())
                    .append(storeDataSourceId, other.getStoreDataSourceId())
                    .append(storeWorkspace, other.getStoreWorkspace())
                    .append(storeLayerName, other.getStoreLayerName()).isEquals();
        }

        return false;
    }

    @Override
    public Map<String, String> toMap(){
        Map<String, String> map = new HashMap<>();
        map.put(RestApiWithEsUtils.SOURCE_TYPE, TYPE);
        map.put(RestApiWithEsUtils.SOURCE_OGC_BASEURI, baseUri);
        map.put(RestApiWithEsUtils.SOURCE_OGC_WMSURI, wmsUri);
        map.put(RestApiWithEsUtils.SOURCE_OGC_WFSURI, wfsUri);
        map.put(RestApiWithEsUtils.SOURCE_OGC_GEOJSONURI, geojsonUri);
        map.put(RestApiWithEsUtils.SOURCE_OGC_STORE_NAME, storeName);
        map.put(RestApiWithEsUtils.SOURCE_OGC_STORE_TYPE, storeType);
        map.put(RestApiWithEsUtils.SOURCE_OGC_STORE_DATASOURCE_ID, storeDataSourceId);
        map.put(RestApiWithEsUtils.SOURCE_OGC_STORE_WORKSPACE, storeWorkspace);
        map.put(RestApiWithEsUtils.SOURCE_OGC_STORE_LAYERNAME, storeLayerName);
        return map;
    }

    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

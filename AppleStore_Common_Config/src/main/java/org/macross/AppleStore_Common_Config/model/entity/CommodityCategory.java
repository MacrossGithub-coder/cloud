package org.macross.AppleStore_Common_Config.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * id	int	类别id
 * describe	varchar	描述
 * create_time	datetime
 */
@Data
@ToString
@JsonInclude(value =JsonInclude.Include.NON_NULL)
public class CommodityCategory {

    private Integer id;

    private String describe;

    @JsonProperty("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    private List<Commodity> commodityList;

}

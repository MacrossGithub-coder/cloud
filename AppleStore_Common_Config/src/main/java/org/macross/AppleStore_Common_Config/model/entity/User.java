package org.macross.AppleStore_Common_Config.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *         id	int 用户id
 *         name	varchar	用户名
 *         pwd	varchar	密码
 *         head_img	varchar	头像
 *         phone	varchar	手机号
 *         account	int	账户金额，单位分
 *         address	varchar	配送地址
 *         create_time	datetime	创建时间
 */
@JsonInclude(value =JsonInclude.Include.NON_NULL)
@Data
@ToString
public class User implements Serializable {

    private Integer id;

    private String name;

    @JsonIgnore
    private String pwd;

    @JsonProperty("head_img")
    private String headImg;

    private String phone;

    private BigDecimal account;

    private String address;

    @JsonProperty("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

}

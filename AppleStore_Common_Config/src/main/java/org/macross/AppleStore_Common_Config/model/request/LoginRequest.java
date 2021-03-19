package org.macross.AppleStore_Common_Config.model.request;

import lombok.Data;

@Data
public class LoginRequest {

    private String phone;

    private String pwd;
}

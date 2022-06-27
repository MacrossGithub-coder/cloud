package org.macross.AppleStore_User_Service_Proj.observer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistration {

    private Integer userId;

    private String userName;

    private String telephone;

    private String e_mail;

    public UserRegistration(String userName, String telephone, String e_mail) {
        this.userName = userName;
        this.telephone = telephone;
        this.e_mail = e_mail;
    }
}

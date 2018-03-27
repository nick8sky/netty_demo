package org.kx.bto;

import lombok.Data;

import java.io.Serializable;

/**
 * create by sunkx on 2018/3/28
 */
@Data
public class UserInfo  implements Serializable{
    private  String name ;
    private  int age ;

    private String info ;


    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", info='" + info + '\'' +
                '}';
    }

    public UserInfo() {
    }

    public UserInfo(String name, int age, String info) {
        this.name = name;
        this.age = age;
        this.info = info;
    }
}

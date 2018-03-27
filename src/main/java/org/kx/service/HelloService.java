package org.kx.service;

import org.kx.bto.UserInfo;

/**
 * create by sunkx on 2018/3/28
 */
public interface HelloService {

    public  void sayHi();


    public UserInfo getUser(String name);
}

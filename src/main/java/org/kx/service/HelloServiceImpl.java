package org.kx.service;


import org.kx.bto.UserInfo;
import org.kx.server.RpcService;

import java.util.Date;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {


    @Override
    public void sayHi() {
        System.out.println("hi " + new Date());

    }

    @Override
    public UserInfo getUser(String name) {
        return new UserInfo("nick",12,"good");
    }
}

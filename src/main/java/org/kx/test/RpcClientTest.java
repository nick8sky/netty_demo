package org.kx.test;


import org.junit.Test;
import org.kx.bto.UserInfo;
import org.kx.registry.NodeInfo;
import org.kx.registry.ServiceRegistry;
import org.kx.rpc.AsyncRPCCallback;
import org.kx.rpc.IAsyncObjectProxy;
import org.kx.rpc.RPCFuture;
import org.kx.rpc.RpcClient;
import org.kx.service.HelloService;
import java.util.concurrent.CountDownLatch;


public class RpcClientTest {

    @Test
    public  void testSyn(){
        ServiceRegistry serviceRegistry = new ServiceRegistry();
        NodeInfo nodeInfo = new  NodeInfo("127.0.0.1",20888);
        serviceRegistry.register(nodeInfo);

        RpcClient rpcClient = new RpcClient();
        HelloService helloService = rpcClient.create(HelloService.class);
        helloService.sayHi();
        System.out.println(helloService.getUser("nick"));
        rpcClient.stop();

    }



    @Test
    public  void testReCall(){
        ServiceRegistry serviceRegistry = new ServiceRegistry();
        NodeInfo nodeInfo = new  NodeInfo("127.0.0.1",20888);
        serviceRegistry.register(nodeInfo);



        RpcClient rpcClient = new RpcClient();
        IAsyncObjectProxy helloService = rpcClient.createAsync(HelloService.class);
        RPCFuture helloPersonFuture = helloService.call("getUser", "xiaoming");

        CountDownLatch countDownLatch = new CountDownLatch(1); //主线程等待
        helloPersonFuture.addCallback(new AsyncRPCCallback() {
            @Override
            public void success(Object result) {
                UserInfo userInfo = (UserInfo) result;
                System.out.println(userInfo);
                System.out.println("do otherthing ...");
                countDownLatch.countDown();
            }

            @Override
            public void fail(Exception e) {
                System.out.println(e);
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        rpcClient.stop();

        System.out.println("End");
    }

}

package org.kx.rpc;

import java.lang.reflect.Proxy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * create by sunkx on 2018/3/27
 */
public class RpcClient {
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));

    //代理同步返回
    public static <T> T create(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ObjectProxy<T>(interfaceClass)
        );
    }

    //返回future返回，没有get,需要手动get
    public static <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
        return new ObjectProxy<T>(interfaceClass);
    }

    //返回后，处理
    protected static void submit(Runnable task){
        threadPoolExecutor.submit(task);
    }


    public void stop() {
        threadPoolExecutor.shutdown();
        ConnectManage.getInstance().stop();
    }


}

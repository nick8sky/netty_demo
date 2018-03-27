package org.kx.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.kx.registry.NodeInfo;
import org.kx.registry.ServiceRegistry;
import org.kx.rpc.RpcDecoder;
import org.kx.rpc.RpcEncoder;
import org.kx.rpc.RpcRequest;
import org.kx.rpc.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * RPC Server
 *
 */
public class RpcServer   {

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);


    private ServiceRegistry serviceRegistry = new ServiceRegistry();
    private NodeInfo nodeInfo ;
    private String beanPacks ;

    private Map<String, Object> handlerMap = new HashMap<>();
    private static ThreadPoolExecutor threadPoolExecutor;

    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;

    public RpcServer(NodeInfo nodeInfo,String beanPacks) {
        this.nodeInfo = nodeInfo;
        this.beanPacks = beanPacks;
    }





    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    public static void submit(Runnable task) {
        if (threadPoolExecutor == null) {
            synchronized (RpcServer.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L,
                            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
                }
            }
        }
        threadPoolExecutor.submit(task);
    }

    public RpcServer addService(String interfaceName, Object serviceBean) {
        if (!handlerMap.containsKey(interfaceName)) {
            logger.info("Loading service: {}", interfaceName);
            handlerMap.put(interfaceName, serviceBean);
        }

        return this;
    }

    private void scanAndMakeBean(String packageName) throws MalformedURLException {
        //把所有的.替换成/
        String url1 = RpcServer.class.getResource("/"+packageName.replaceAll("\\.", "/")).getPath();
        if(url1.startsWith("file:")) url1 = url1.substring(5);
        //URL url  = new URL(url1);
        //System.out.println(url1);
        File dir = new File(url1);
        for (File file : dir.listFiles()) {
            if(file.isDirectory()){
                //递归读取包
                scanAndMakeBean(packageName+"."+file.getName());
            }else{
                String className =packageName +"." +file.getName().replace(".class", "");
                try {
                    //把类搞出来,反射来实例化(只有加@RpcService需要实例化)
                    Class<?> clazz =Class.forName(className);
                    if(clazz.isAnnotationPresent(RpcService.class)){
                        String interfaceName =clazz.getAnnotation(RpcService.class).value().getName();
                        handlerMap.put(interfaceName,clazz.newInstance());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }
    public void start() throws Exception {
        //scan bean
        scanAndMakeBean(beanPacks);



        if (bossGroup == null && workerGroup == null) {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
                                    .addLast(new RpcDecoder(RpcRequest.class))
                                    .addLast(new RpcEncoder(RpcResponse.class))
                                    .addLast(new RpcHandler(handlerMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);



            ChannelFuture future = bootstrap.bind(nodeInfo.getIp(), nodeInfo.getPort()).sync();
            logger.info("Server started on port {}", nodeInfo.getPort());

            if (serviceRegistry != null) {
                serviceRegistry.register(nodeInfo);
            }

            future.channel().closeFuture().sync();
        }
    }


}

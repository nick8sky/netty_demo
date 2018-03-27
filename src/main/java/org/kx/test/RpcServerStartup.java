package org.kx.test;


import org.kx.registry.NodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcServerStartup {
    private static final Logger logger = LoggerFactory.getLogger(RpcServerStartup.class);

    public static void main(String[] args) throws InterruptedException {
        NodeInfo nodeInfo = new  NodeInfo("127.0.0.1",20888);
        org.kx.server.RpcServer rpcServer = new org.kx.server.RpcServer(nodeInfo,"org.kx.service");
        try {
            rpcServer.start();
        } catch (Exception ex) {
            logger.error("Exception: {}", ex);
        }
        System.out.println("RpcServerStartup ...");
    }
}

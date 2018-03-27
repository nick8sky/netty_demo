package org.kx.registry;

import org.kx.rpc.ConnectManage;

import java.util.ArrayList;
import java.util.List;

/**
 * create by sunkx on 2018/3/28
 */
public class ServiceRegistry {


    static  List<NodeInfo> nodes = new ArrayList<>();

    public void register(NodeInfo nodeInfo) {
        nodes.add(nodeInfo);
        //默认添加一个 用于测试 TODO
        NodeInfo nodeInfo2 = new NodeInfo("127.0.0.1",20888);
        nodes.add(nodeInfo2);

        ConnectManage.getInstance().updateConnectedServer(nodes);
    }




}

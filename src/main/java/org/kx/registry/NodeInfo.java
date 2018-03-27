package org.kx.registry;

import lombok.Data;

/**
 * create by sunkx on 2018/3/28
 */

@Data
public class NodeInfo {
    private  String ip;

    private  int port;

    public NodeInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}

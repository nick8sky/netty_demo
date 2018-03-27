package org.kx.rpc;

import lombok.Data;

/**
 * create by sunkx on 2018/3/27
 */
@Data
public class RpcResponse {
    private String requestId;
    private String error;
    private Object result;

    public boolean isError() {
        return error != null;
    }
}

package org.kx.rpc;

import lombok.Data;

/**
 * create by sunkx on 2018/3/27
 */

@Data
public class RpcRequest {
    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;


}

package org.kx.rpc;


/**
 */
public interface IAsyncObjectProxy {
    public RPCFuture call(String funcName, Object... args);
}
package com.gloryofme.zkrpc.common.bean;

/**
 * rpc响应
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public class ZkRpcResponse {

    private Object result;
    private Exception exception;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}

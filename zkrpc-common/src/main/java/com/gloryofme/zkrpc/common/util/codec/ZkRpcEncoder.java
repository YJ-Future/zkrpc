package com.gloryofme.zkrpc.common.util.codec;

import com.gloryofme.zkrpc.common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

/**
 * RPC编码器
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public class ZkRpcEncoder extends MessageToByteEncoder {

    private Class<?> clazz;

    public ZkRpcEncoder(Class<?> clazz){
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if(clazz.isInstance(msg)){
            byte [] data = SerializationUtil.serialize(msg);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}

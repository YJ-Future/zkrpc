package com.gloryofme.zkrpc.common.util.codec;

import com.gloryofme.zkrpc.common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.commons.lang3.SerializationUtils;

import java.util.List;

/**
 * RPC解码器
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public class ZkRpcDecoder extends ByteToMessageDecoder {
    private Class<?> clazz;

    public ZkRpcDecoder(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < 4)
            return ;
        in.markReaderIndex();
        int dataLength = in.readInt();
        if(in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }
        byte []data = new byte[dataLength];
        in.readBytes(data);
        out.add(SerializationUtil.deserialize(data,clazz));
    }
}

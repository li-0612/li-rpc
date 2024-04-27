package com.liyan.rpc.protocol;


import com.liyan.rpc.serializer.Serializer;
import com.liyan.rpc.serializer.SerializerFactory;
import io.netty.buffer.search.SearchProcessorFactory;
import io.vertx.core.buffer.Buffer;

public class ProtocolMessageEncoder {
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws Exception{
        if (protocolMessage == null || protocolMessage.getHeader() == null) {
            return Buffer.buffer();
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();
        //依次向缓冲区写入字节
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());
//        buffer.appendInt(header.getBodyLength());
        //获取序列化器
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new Exception("serializer not found");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        byte[] bytes = serializer.serialize(protocolMessage.getBody());
        //写入body长度和数据
        buffer.appendInt(bytes.length);
        buffer.appendBytes(bytes);
        return buffer;
    }
}

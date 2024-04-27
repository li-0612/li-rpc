package com.liyan.rpc.server.tcp;

import com.liyan.rpc.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * 装饰者模式 （使用recordParser 对原有的buffer处理能力进行增强）
 */
@Slf4j
public class TcpBufferHandlerWrapper implements Handler<Buffer> {
    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        this.recordParser = initRecordParser(bufferHandler);
    }

    /**
     * 初始化 recordParser
     * @param bufferHandler
     * @return
     */
    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        //构造parser解决半包粘包问题
        RecordParser recordParser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);

        recordParser.setOutput(new Handler<Buffer>() {
            //初始化
            int size = -1;
            //一次完整的读取（头+体）
            Buffer resultBuffer = Buffer.buffer();


            @Override
            public void handle(Buffer buffer) {
                if (size == -1) {
//                    System.out.println("buffer.toString() = " + buffer.toString(StandardCharsets.UTF_8));
                    //读取消息体长度
                    size = buffer.getInt(13);
                    recordParser.fixedSizeMode(size);
                    //写入头信息到结果
                    resultBuffer.appendBuffer(buffer);
                } else {
                    //写入体信息到结果
                    resultBuffer.appendBuffer(buffer);
//                    System.out.println("resultBuffer = " + resultBuffer.toString(StandardCharsets.UTF_8));
                    //已拼接为完整buffer，执行处理
                    bufferHandler.handle(resultBuffer);
                    //重置一轮
                    recordParser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
//                        String str = new String(buffer.getBytes());
//                        System.out.println("str = " + str);
//                        if (testMessage.equals(str)) {
//                            System.out.println("good = ");
//                        }
            }
        });
        return recordParser;
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }
}

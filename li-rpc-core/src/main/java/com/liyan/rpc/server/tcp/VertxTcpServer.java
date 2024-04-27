package com.liyan.rpc.server.tcp;

import com.liyan.rpc.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
@Slf4j
public class VertxTcpServer implements HttpServer {
    @Override
    public void start(int port) {
        //创建vert.x 实例
        Vertx vertx = Vertx.vertx();
        //创建tcp服务器
        NetServer server = vertx.createNetServer();
        server.connectHandler(
                new TcpServerHandler()
        );
        //启动tcp服务器并监听端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("TCP server started on port {}", port);
            } else {
                log.error("Failed to start TCP server: {}", result.cause());
            }
        });
    }

    private byte[] handleRequest(byte[] requestData) {
        //在这里编写处理请求的逻辑，根据请求数据requestData构造响应数据并返回
        //这里只是一个示例，实际的逻辑需要根据具体的业务需求来实现
        return "Hello, world!".getBytes();
    }

    public static void main(String[] args) {
        new VertxTcpServer().start(8084);
    }

}

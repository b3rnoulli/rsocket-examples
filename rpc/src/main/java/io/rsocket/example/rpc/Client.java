package io.rsocket.example.rpc;

import com.rsocket.rpc.Request;
import com.rsocket.rpc.ServiceClient;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client {

    public static void main(String[] args) {
        RSocket rSocket = RSocketFactory
                .connect()
                .transport(TcpClientTransport.create(7000))
                .start()
                .block();
        ServiceClient serviceClient = new ServiceClient(rSocket);

        serviceClient.fireAndForget(Request.newBuilder()
                .setMessage("world!").build())
                .block();

        serviceClient.requestResponse(Request.newBuilder()
                .setMessage("world!").build())
                .doOnNext(response -> log.info("Received response: [{}]", response))
                .block();

    }

}

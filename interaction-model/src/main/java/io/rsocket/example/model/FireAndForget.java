package io.rsocket.example.model;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;

import static io.rsocket.example.model.Server.HOST;
import static io.rsocket.example.model.Server.PORT;

public class FireAndForget {

    public static void main(String[] args) {
        RSocket socket = RSocketFactory.connect()
                .transport(TcpClientTransport.create(HOST, PORT))
                .start()
                .block();

        socket.fireAndForget(DefaultPayload.create("Hello world!")).block();

    }

}

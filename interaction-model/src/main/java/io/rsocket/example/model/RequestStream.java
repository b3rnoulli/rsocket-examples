package io.rsocket.example.model;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;

import static io.rsocket.example.model.Server.HOST;
import static io.rsocket.example.model.Server.PORT;

public class RequestStream {

    public static void main(String[] args) {

        RSocket socket = RSocketFactory.connect()
                .transport(TcpClientTransport.create(HOST, PORT))
                .start()
                .block();
        socket.requestStream(DefaultPayload.create("Jenny", "example-metadata"))
                .limitRequest(5)
                .map(Payload::getDataUtf8)
                .blockLast();

        socket.dispose();
    }


}

package io.rsocket.example.resumability;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;

import static io.rsocket.example.resumability.ResumableServer.HOST;
import static io.rsocket.example.resumability.ResumableServer.RESUME_SESSION_DURATION;

@Slf4j
public class ResumableClient {

    private static final int CLIENT_PORT = 7001;

    public static void main(String[] args) {
        RSocket socket = RSocketFactory.connect()
                .resume()
                .resumeSessionDuration(RESUME_SESSION_DURATION)
                .transport(TcpClientTransport.create(HOST, CLIENT_PORT))
                .start()
                .block();
        socket.requestStream(DefaultPayload.create("dummy"))
                .map(payload -> {
                    log.info("Received data: [{}]", payload.getDataUtf8());
                    return payload;
                })
                .blockLast();

    }
}

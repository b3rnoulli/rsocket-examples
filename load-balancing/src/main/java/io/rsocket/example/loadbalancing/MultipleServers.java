package io.rsocket.example.loadbalancing;

import io.rsocket.*;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Slf4j
public class MultipleServers {

    static final String HOST = "localhost";
    static final int[] PORTS = new int[]{7000, 7001, 7002};


    public static void main(String[] args) throws InterruptedException {

        Arrays.stream(PORTS)
                .forEach(port -> RSocketFactory.receive()
                        .acceptor(new SimpleSocketAcceptor("SERVER-" + port))
                        .transport(TcpServerTransport.create(HOST, port))
                        .start()
                        .subscribe());

        log.info("Servers running");

        Thread.currentThread().join();
    }

    static class SimpleSocketAcceptor implements SocketAcceptor {

        private String serverName;

        SimpleSocketAcceptor(String serverName) {
            this.serverName = serverName;
        }

        @Override
        public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket sendingSocket) {
            log.info("Received setup connection on acceptor: [{}]", serverName);
            return Mono.just(new AbstractRSocket() {
                @Override
                public Mono<Payload> requestResponse(Payload payload) {
                    log.info("Received 'request response' request with payload: [{}] on server [{}]",
                            payload.getDataUtf8(), serverName);
                    return Mono.just(DefaultPayload.create("test-response"));
                }
            });
        }
    }

}

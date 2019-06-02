package io.rsocket.example.rpc;

import com.google.protobuf.Empty;
import com.rsocket.rpc.Request;
import com.rsocket.rpc.Response;
import com.rsocket.rpc.Service;
import com.rsocket.rpc.ServiceServer;
import io.netty.buffer.ByteBuf;
import io.rsocket.RSocketFactory;
import io.rsocket.rpc.rsocket.RequestHandlingRSocket;
import io.rsocket.transport.netty.server.TcpServerTransport;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
public class Server {

    public static void main(String[] args) throws InterruptedException {

        ServiceServer serviceServer = new ServiceServer(new DefaultService(), Optional.empty(), Optional.empty());

        RSocketFactory
                .receive()
                .acceptor((setup, sendingSocket) -> Mono.just(
                        new RequestHandlingRSocket(serviceServer)
                ))
                .transport(TcpServerTransport.create(7000))
                .start()
                .block();

        Thread.currentThread().join();
    }


    static class DefaultService implements Service {

        @Override
        public Mono<Empty> fireAndForget(Request message, ByteBuf metadata) {
            log.info("Received 'fire and forget' request with body: [{}]", message);
            return Mono.just(Empty.getDefaultInstance());
        }

        @Override
        public Mono<Response> requestResponse(Request message, ByteBuf metadata) {
            log.info("Received 'request response' request with body: [{}]", message);
            return Mono.fromCallable(() -> Response.newBuilder()
                    .setMessage("Hello " + message.getMessage())
                    .build());
        }

        @Override
        public Flux<Response> requestStream(Request message, ByteBuf metadata) {
            return null;
        }
    }

}

package io.rsocket.example.resumability;

import io.rsocket.*;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
public class ResumableResponder {

    private static final int SERVER_PORT = 7000;
    static final String HOST = "localhost";
    static final Duration RESUME_SESSION_DURATION = Duration.ofSeconds(60);


    public static void main(String[] args) throws InterruptedException {

        RSocketFactory.receive()
                .resume()
                .resumeSessionDuration(RESUME_SESSION_DURATION)
                .acceptor((setup, sendingSocket) -> Mono.just(new AbstractRSocket() {
                    @Override
                    public Flux<Payload> requestStream(Payload payload) {
                        log.info("Received 'requestStream' request with payload: [{}]", payload.getDataUtf8());
                        return Flux.interval(Duration.ofMillis(1000))
                                .map(t -> DefaultPayload.create(t.toString()));
                    }
                }))
                .transport(TcpServerTransport.create(HOST, SERVER_PORT))
                .start()
                .subscribe();
        log.info("Server running");

        Thread.currentThread().join();
    }

}

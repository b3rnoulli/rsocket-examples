package io.rsocket.example.model;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import static io.rsocket.example.model.Server.HOST;
import static io.rsocket.example.model.Server.PORT;

public class RequestStream {

    public static void main(String[] args) {

        RSocket socket = RSocketFactory.connect()
                .transport(TcpClientTransport.create(HOST, PORT))
                .start()
                .block();
        socket.requestStream(DefaultPayload.create("Jenny", "example-metadata"))
                .limitRequest(100)
                .subscribe(new BackPressureSubscriber());

        socket.dispose();
    }

    @Slf4j
    private static class BackPressureSubscriber implements Subscriber<Payload> {

        private static final Integer NUMBER_OF_REQUESTS_TO_PROCESS = 5;
        private Subscription subscription;
        int receivedItems;

        @Override
        public void onSubscribe(Subscription s) {
            this.subscription = s;
            subscription.request(NUMBER_OF_REQUESTS_TO_PROCESS);
        }

        @Override
        public void onNext(Payload payload) {
            receivedItems++;
            if (receivedItems % NUMBER_OF_REQUESTS_TO_PROCESS == 0) {
                log.info("Requesting next [{}] elements");
                subscription.request(NUMBER_OF_REQUESTS_TO_PROCESS);
            }
        }

        @Override
        public void onError(Throwable t) {
            log.error("Stream subscription error [{}]", t);
        }

        @Override
        public void onComplete() {
            log.info("Completing subscription");
        }
    }

}

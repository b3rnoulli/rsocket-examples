package io.rsocket.example.rpc;

import com.google.protobuf.Empty;
import com.rsocket.rpc.*;
import io.netty.buffer.ByteBuf;
import io.rsocket.RSocketFactory;
import io.rsocket.rpc.rsocket.RequestHandlingRSocket;
import io.rsocket.transport.netty.server.TcpServerTransport;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

@Slf4j
public class Server {

    public static void main(String[] args) throws InterruptedException {

        CustomerServiceServer serviceServer = new CustomerServiceServer(new DefaultCustomerService(), Optional.empty(), Optional.empty());

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


    static class DefaultCustomerService implements CustomerService {

        private static final List<String> RANDOM_NAMES = Arrays.asList("Andrew", "Joe", "Matt", "Rachel", "Robin", "Jack");

        @Override
        public Mono<CustomerResponse> getCustomer(SingleCustomerRequest message, ByteBuf metadata) {
            log.info("Received 'getCustomer' request [{}]", message);
            return Mono.just(CustomerResponse.newBuilder()
                    .setId(message.getId())
                    .setName(getRandomName())
                    .build());
        }

        @Override
        public Flux<CustomerResponse> getCustomers(MultipleCustomersRequest message, ByteBuf metadata) {
            return Flux.interval(Duration.ofMillis(1000))
                    .map(time -> CustomerResponse.newBuilder()
                            .setId(UUID.randomUUID().toString())
                            .setName(getRandomName())
                            .build());
        }

        @Override
        public Mono<Empty> deleteCustomer(SingleCustomerRequest message, ByteBuf metadata) {
            log.info("Received 'deleteCustomer' request [{}]", message);
            return Mono.just(Empty.newBuilder().build());
        }

        @Override
        public Flux<CustomerResponse> customerChannel(Publisher<MultipleCustomersRequest> messages, ByteBuf metadata) {
            return Flux.from(messages)
                    .doOnNext(message -> log.info("Received 'customerChannel' request [{}]", message))
                    .map(message -> CustomerResponse.newBuilder()
                            .setId(UUID.randomUUID().toString())
                            .setName(getRandomName())
                            .build());
        }

        private String getRandomName() {
            return RANDOM_NAMES.get(new Random().nextInt(RANDOM_NAMES.size() - 1));
        }
    }

}

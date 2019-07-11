package io.rsocket.example.loadbalancing;

import io.rsocket.RSocketFactory;
import io.rsocket.client.LoadBalancedRSocketMono;
import io.rsocket.client.filter.RSocketSupplier;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static io.rsocket.example.loadbalancing.MultipleServers.HOST;

@Slf4j
public class LoadBalancedClient {

    static final int[] PORTS = new int[]{7000, 7001, 7002};

    public static void main(String[] args) {

        List<RSocketSupplier> rsocketSuppliers = Arrays.stream(PORTS)
                .mapToObj(port -> new RSocketSupplier(() -> RSocketFactory.connect()
                        .transport(TcpClientTransport.create(HOST, port))
                        .start()))
                .collect(Collectors.toList());

        LoadBalancedRSocketMono balancer = LoadBalancedRSocketMono.create((Publisher<Collection<RSocketSupplier>>) s -> {
            s.onNext(rsocketSuppliers);
            s.onComplete();
        });

        Flux.range(0, 10000)
                .flatMap(i -> balancer)
                .doOnNext(rSocket -> rSocket.requestResponse(DefaultPayload.create("test-request")).block())
                .blockLast();

        int a =2;

    }

}

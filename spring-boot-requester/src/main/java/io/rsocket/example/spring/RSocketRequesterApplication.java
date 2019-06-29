package io.rsocket.example.spring;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Slf4j
@SpringBootApplication
public class RSocketRequesterApplication {


    public static void main(String[] args) {
        SpringApplication.run(RSocketRequesterApplication.class);
    }

    @Bean
    RSocket rSocket() {
        return RSocketFactory
                .connect()
                .frameDecoder(PayloadDecoder.ZERO_COPY)
                .dataMimeType(MimeTypeUtils.APPLICATION_JSON_VALUE)
                .transport(TcpClientTransport.create(7000))
                .start()
                .block();
    }

    @Bean
    RSocketRequester rSocketRequester(RSocket rSocket, RSocketStrategies rSocketStrategies) {
        return RSocketRequester.wrap(rSocket, MimeTypeUtils.APPLICATION_JSON,
                rSocketStrategies);
    }

    @RestController
    class CustomerController {

        private final CustomerServiceAdapter customerServiceAdapter;


        CustomerController(CustomerServiceAdapter customerServiceAdapter) {
            this.customerServiceAdapter = customerServiceAdapter;
        }

        @GetMapping("/customers/{id}")
        Mono<CustomerResponse> getCustomer(@PathVariable String id) {
            return customerServiceAdapter.getCustomer(id);
        }

        @GetMapping(value = "/customers", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
        Publisher<CustomerResponse> getCustomers() {
            return customerServiceAdapter.getCustomers(getRandomIds(10));
        }

        @GetMapping(value = "/customers-channel", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
        Publisher<CustomerResponse> getCustomersChannel() {
            return customerServiceAdapter.getCustomerChannel(Flux.interval(Duration.ofMillis(1000))
                    .map(id -> new CustomerRequest(UUID.randomUUID().toString())));
        }

        private List<String> getRandomIds(int amount) {
            return IntStream.range(0, amount)
                    .mapToObj(n -> UUID.randomUUID().toString())
                    .collect(toList());
        }

    }

    @Component
    class CustomerServiceAdapter {

        private final RSocketRequester rSocketRequester;

        CustomerServiceAdapter(RSocketRequester rSocketRequester) {
            this.rSocketRequester = rSocketRequester;
        }

        Mono<CustomerResponse> getCustomer(String id) {
            return rSocketRequester
                    .route("customer")
                    .data(new CustomerRequest(id))
                    .retrieveMono(CustomerResponse.class)
                    .doOnNext(customerResponse -> log.info("Received customer as mono [{}]", customerResponse));
        }

        Flux<CustomerResponse> getCustomers(List<String> ids) {
            return rSocketRequester
                    .route("customer-stream")
                    .data(new MultipleCustomersRequest(ids))
                    .retrieveFlux(CustomerResponse.class)
                    .doOnNext(customerResponse -> log.info("Received customer as flux [{}]", customerResponse));
        }

        Flux<CustomerResponse> getCustomerChannel(Flux<CustomerRequest> customerRequestFlux) {
            return rSocketRequester
                    .route("customer-channel")
                    .data(customerRequestFlux, CustomerRequest.class)
                    .retrieveFlux(CustomerResponse.class)
                    .doOnNext(customerResponse -> log.info("Received customer as flux [{}]", customerResponse));
        }
    }

}
@Getter
@ToString
class CustomerRequest {
    private String id;

    public CustomerRequest() {
    }

    CustomerRequest(String id) {
        this.id = id;
    }
}

@Getter
@ToString
class MultipleCustomersRequest {
    private List<String> ids;

    public MultipleCustomersRequest() {
    }

    MultipleCustomersRequest(List<String> ids) {
        this.ids = ids;
    }
}


@Getter
@ToString
class CustomerResponse {

    private String id;

    private String name;

    public CustomerResponse() {
    }

    CustomerResponse(String id, String name) {
        this.id = id;
        this.name = name;
    }
}

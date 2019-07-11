package io.rsocket.example.spring;


import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


@Slf4j
@SpringBootApplication
public class RSocketResponderApplication {


    public static void main(String[] args) {
        SpringApplication.run(RSocketResponderApplication.class);
    }

    @Controller
    public class CustomerController {

        private final List<String> RANDOM_NAMES = Arrays.asList("Andrew", "Joe", "Matt", "Rachel", "Robin", "Jack");

        @MessageMapping("customer")
        CustomerResponse getCustomer(CustomerRequest customerRequest) {
            return new CustomerResponse(customerRequest.getId(), getRandomName());
        }

        @MessageMapping("customer-stream")
        Flux<CustomerResponse> getCustomers(MultipleCustomersRequest multipleCustomersRequest) {
            return Flux.range(0, multipleCustomersRequest.getIds().size())
                    .delayElements(Duration.ofMillis(500))
                    .map(i -> new CustomerResponse(multipleCustomersRequest.getIds().get(i), getRandomName()));
        }

        @MessageMapping("customer-channel")
        Flux<CustomerResponse> getCustomersChannel(Flux<CustomerRequest> requests) {
            return Flux.from(requests)
                    .doOnNext(message -> log.info("Received 'customerChannel' request [{}]", message))
                    .map(message -> new CustomerResponse(message.getId(), getRandomName()));
        }

        private String getRandomName() {
            return RANDOM_NAMES.get(new Random().nextInt(RANDOM_NAMES.size() - 1));
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

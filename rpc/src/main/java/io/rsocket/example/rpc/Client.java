package io.rsocket.example.rpc;

import com.rsocket.rpc.CustomerServiceClient;
import com.rsocket.rpc.MultipleCustomersRequest;
import com.rsocket.rpc.SingleCustomerRequest;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class Client {

    public static void main(String[] args) {
        RSocket rSocket = RSocketFactory
                .connect()
                .transport(TcpClientTransport.create(7000))
                .start()
                .block();
        CustomerServiceClient customerServiceClient = new CustomerServiceClient(rSocket);

        customerServiceClient.deleteCustomer(SingleCustomerRequest.newBuilder()
                .setId(UUID.randomUUID().toString()).build())
                .block();

        customerServiceClient.getCustomer(SingleCustomerRequest.newBuilder()
                .setId(UUID.randomUUID().toString()).build())
                .doOnNext(response -> log.info("Received response for 'getCustomer': [{}]", response))
                .block();

        customerServiceClient.getCustomers(MultipleCustomersRequest.newBuilder()
                .addIds(UUID.randomUUID().toString()).build())
                .doOnNext(response -> log.info("Received response for 'getCustomers': [{}]", response))
                .subscribe();

        customerServiceClient.customerChannel(s -> s.onNext(MultipleCustomersRequest.newBuilder()
                .addIds(UUID.randomUUID().toString())
                .build()))
                .doOnNext(customerResponse -> log.info("Received response for 'customerChannel' [{}]", customerResponse))
                .blockLast();
    }

}

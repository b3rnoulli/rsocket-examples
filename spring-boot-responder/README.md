# Spring Boot Responder

This repository contains example simple RSocket requester built as a spring boot application. 
It expose three endpoints:

- `customers`
- `customers/{id}`
- `customers-channel`

which are routed to the Spring Boot Responder application using RSocket as a transport layer.
In order to run this example please start the Responder application first 
and then spin up the requester.

More details about RSocket and Spring Boot integration can be found here: https://blog.grapeup.com/read/reactive-service-to-service-communication-with-rsocket-abstraction-over-the-rsocket-66

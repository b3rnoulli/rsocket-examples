# RPC

This module contains example application which leverages RPC over the RSocket with Protobuf as a serialization mechanism
The RPC interface definition and transport objects are are defined in `service.proto` file. 
In order to generate required classes please use `gradle clean build` or `generateProto` command.


More details about RPC over the RSocket are available here: https://blog.grapeup.com/read/reactive-service-to-service-communication-with-rsocket-abstraction-over-the-rsocket-66

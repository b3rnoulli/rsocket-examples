# RSocket Examples


## Introduction

This repository contains examples used in the series of articles about RSocket. The articles are available here: https://medium.com/@b3rnoulli/reactive-service-to-service-communication-with-rsocket-introduction-5d64e5b6909

It consist of following modules:
- interaction-model
- load-balancing
- resumability
- rpc
- spring-boot-requester
- spring-boot-responder

Each module address different aspect of the protocol, more detailed description is available in the module directories.

## Build

The modules use ```gradle``` as a build tool. In order to crate executable jars please invoke
`./gradlew clean build` on the root directory. Each module can be built individually using the same command, 
but executed in the particular module directory.

Please notice that examples were designed to run inside your IDE.

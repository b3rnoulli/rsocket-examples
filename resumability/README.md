# Resumability

This module contains example of resumability mechanism built-in RSocket. 
It consist of `ResumableRequester` and `ResumableResponder` which exchange data using
request-stream method from interaction model. The responder expose server on port `7000` 
while requester connects to the port `7001`
In order to run it please use `socat` with following command:

`socat -d TCP-LISTEN:7001,fork,reuseaddr TCP:localhost:7000`

It creates a mapping between ports `7000` and `7001`, so that the requester can reach the responder. 
If you would like to simulate network connectivity issues please stop and start `socat` while applications are running.
Then, you should see *RESUME* and *RESUME_OK* frames in the logs:

```11:22:06.932 [parallel-6] DEBUG io.rsocket.resume.ClientRSocketSession - Retrying with: ExponentialBackoffResumeStrategy{next=PT8S, firstBackoff=PT1S, maxBackoff=PT16S, factor=2}
   11:22:22.939 [reactor-tcp-nio-2] DEBUG reactor.netty.channel.FluxReceive - [id: 0xa03e0798, L:/127.0.0.1:60895 - R:localhost/127.0.0.1:7001] Subscribing inbound receiver [pending: 0, cancelled:false, inboundDone: false]
   11:22:22.939 [reactor-tcp-nio-2] DEBUG io.rsocket.resume.ResumableDuplexConnection - client Resumable duplex connection reconnected with connection: io.rsocket.internal.ClientServerInputMultiplexer$InternalDuplexConnection@60ef1f7e
   11:22:22.941 [reactor-tcp-nio-2] DEBUG io.rsocket.resume.ResumableDuplexConnection - Switching transport: client
   11:22:22.941 [reactor-tcp-nio-2] DEBUG io.rsocket.resume.UpstreamFramesSubscriber - Upstream subscriber requestN: 128
   11:22:22.941 [reactor-tcp-nio-2] DEBUG io.rsocket.resume.ClientRSocketSession - Client ResumableConnection reconnected. Sending RESUME frame with state: [impliedPos: 94, pos: 0]
   11:22:22.941 [reactor-tcp-nio-2] DEBUG io.rsocket.FrameLogger - sending -> 
   Frame => Stream ID: 0 Type: RESUME Flags: 0b0 Length: 44
   Data:
   
   11:22:22.943 [reactor-tcp-nio-2] DEBUG reactor.netty.ReactorNetty - [id: 0xa03e0798, L:/127.0.0.1:60895 - R:localhost/127.0.0.1:7001] Added decoder [RSocketLengthCodec] at the end of the user pipeline, full pipeline: [RSocketLengthCodec, reactor.right.reactiveBridge, DefaultChannelPipeline$TailContext#0]
   11:22:22.943 [reactor-tcp-nio-2] DEBUG reactor.netty.resources.PooledConnectionProvider - [id: 0xa03e0798, L:/127.0.0.1:60895 - R:localhost/127.0.0.1:7001] Channel connected, now 1 active connections and 0 inactive connections
   11:22:22.968 [reactor-tcp-nio-2] DEBUG io.rsocket.FrameLogger - receiving -> 
   Frame => Stream ID: 0 Type: RESUME_OK Flags: 0b0 Length: 14
   Data:
   
   11:22:22.968 [reactor-tcp-nio-2] DEBUG io.rsocket.resume.ClientRSocketSession - ResumeOK FRAME received
```


To find out more about resumability mechanism in RSocket see: https://blog.grapeup.com/read/reactive-service-to-service-communication-with-rsocket-load-balancing--resumability-65

package net;

public class HelloServerTest extends APIHelloServerReferenceTest {
    @Override
    API.HelloServer getNewHelloServer() {
        return new HelloServer();
    }
}
package net;

public class HelloClientTest extends APIHelloClientReferenceTest {
    @Override
    API.HelloClient getNewHelloClient() {
        return new HelloClient();
    }
}
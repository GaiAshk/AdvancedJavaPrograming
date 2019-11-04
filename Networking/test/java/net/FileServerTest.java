package net;

public class FileServerTest extends APIFileServerReferenceTest {
    @Override
    API.FileServer getNewFileServer() {
        return new FileServer();
    }
}

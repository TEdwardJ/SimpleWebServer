package edu.ted.server;

import java.io.File;

public class StaticResource extends AbstractResource {

    private File resource;

    private byte[] content;

    public File getResource() {
        return resource;
    }

    public void setResource(File resource) {
        this.resource = resource;
    }

    @Override
    public byte[] getResourceContent() {
        return content;
    }

    public void setResourceContent(byte[] content) {
        this.content = content;
    }

    public void setResourceContent(String content) {
        this.content = content.getBytes();
    }
}

package edu.ted.entity;

import java.io.File;

public class StaticResource {
    private String resourceType;
    private File resource;

    private byte[] content;

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceType() {
        return resourceType;
    }

    public File getResource() {
        return resource;
    }

    public void setResource(File resource) {
        this.resource = resource;
    }

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

package edu.ted.entity;

public class StaticResource {
    private String resourceType;

    private byte[] content;

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceType() {
        return resourceType;
    }

    public byte[] getResourceContent() {
        return content;
    }

    public void setResourceContent(byte[] content) {
        this.content = content;
    }
}

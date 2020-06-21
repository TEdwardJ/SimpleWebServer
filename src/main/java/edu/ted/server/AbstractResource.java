package edu.ted.server;

public abstract class AbstractResource implements Resource {
    private String resourceType;

    @Override
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public String getResourceType() {
        return resourceType;
    }

    @Override
    abstract public byte[] getResourceContent();
}

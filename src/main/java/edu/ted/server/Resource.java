package edu.ted.server;

public interface Resource {
    void setResourceType(String resourceType);
    String getResourceType();
    byte[] getResourceContent();
}

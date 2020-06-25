package edu.ted.io;

import edu.ted.entity.HttpResponseCode;
import edu.ted.entity.StaticResource;
import edu.ted.exception.ServerException;

import java.io.*;

public final class ResourceReader {

    private ResourceReader() {
        throw new AssertionError("No edu.ted.io.ResourceReader instances for you!");
    }

    static String resolveResourcePath(String resource) {
        if (resource.equals("/")) {
            return "/index.html";
        }
        return resource;
    }

    static String resolveResourceType(String path) {
        String[] pathFragments = path.split("\\.");
        String extension = pathFragments[1].toLowerCase();
        if ("html".equals(extension) || "htm".equals(extension)) {
            return "text/html; charset=utf-8";
        } else if ("jpg".equals(extension) || "jpeg".equals(extension)) {
            return "image/jpeg";
        } else if ("bmp".equals(extension)) {
            return "image/bmp";
        } else if ("gif".equals(extension)) {
            return "image/gif";
        } else if ("ico".equals(extension)) {
            return "image/x-icon";
        } else if ("png".equals(extension)) {
            return "image/png";
        } else if ("pdf".equals(extension)) {
            return "application/pdf";
        } else if ("txt".equals(extension)) {
            return "text/plain; charset=utf-8";
        }
        return "application/octet-stream";
    }

    public static StaticResource getResource(String resourceLocation, String rootDirectory) {
        String resolvedResourcelocation = resolveResourcePath(resourceLocation);
        File resourceFile = new File(rootDirectory + resolvedResourcelocation);

        if (!resourceFile.exists()) {
            throw new ServerException(HttpResponseCode.NOT_FOUND);
        }
        StaticResource resource = new StaticResource();
        resource.setResourceType(resolveResourceType(resolvedResourcelocation));
        resource.setResourceContent(readStaticResource(resourceFile));

        return resource;
    }

    static byte[] readStaticResource(File resourceFile) {
        try (InputStream source = new BufferedInputStream(new FileInputStream(resourceFile));
             ByteArrayOutputStream byteContent = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int count;
            while ((count = source.read(buffer)) > -1) {
                byteContent.write(buffer, 0, count);
            }
            return byteContent.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }
}

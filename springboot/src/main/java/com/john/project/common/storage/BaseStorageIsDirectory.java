package com.john.project.common.storage;

import org.springframework.core.io.ByteArrayResource;
import jakarta.servlet.http.HttpServletRequest;

public abstract class BaseStorageIsDirectory extends BaseStorageGetResourceForRequest {

    public Boolean isDirectory(HttpServletRequest request) {
        try {
            var resource = this.getResourceFromRequest(request);
            var isFolder = resource instanceof ByteArrayResource;
            return isFolder;
        } catch (Throwable e) {
            return false;
        }
    }

}

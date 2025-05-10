package com.john.project.properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class StorageRootPathProperties {

    @Value("${properties.storage.root.path}")
    private String storageRootPath;

    public boolean getIsUnitTestEnvironment() {
        return StringUtils.equals(storageRootPath, "defaultTest-a56b075f-102e-edf3-8599-ffc526ec948a");
    }

}

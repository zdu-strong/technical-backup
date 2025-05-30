package com.john.project.common.uuid;

import com.fasterxml.uuid.Generators;
import org.springframework.stereotype.Component;

@Component
public class UUIDUtil {

    public String v4() {
        return Generators.randomBasedGenerator().generate().toString();
    }

    public String v7() {
        return Generators.timeBasedEpochRandomGenerator().generate().toString();
    }

}

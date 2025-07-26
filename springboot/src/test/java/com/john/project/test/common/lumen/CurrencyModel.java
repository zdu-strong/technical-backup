package com.john.project.test.common.lumen;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CurrencyModel {

    private String id;

    private String name;

}

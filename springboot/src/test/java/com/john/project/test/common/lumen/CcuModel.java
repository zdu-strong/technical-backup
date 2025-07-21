package com.john.project.test.common.lumen;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class CcuModel {

    private String id;

    private String name;

    private List<CurrencyModel> childList;

}

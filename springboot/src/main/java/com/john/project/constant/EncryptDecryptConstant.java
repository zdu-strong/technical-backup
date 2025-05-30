package com.john.project.constant;

import com.john.project.properties.DatabaseJdbcProperties;
import cn.hutool.extra.spring.SpringUtil;

public class EncryptDecryptConstant {

    public static String getId() {
        var isNewSqlDatabase = SpringUtil.getBean(DatabaseJdbcProperties.class).getIsNewSqlDatabase();
        if (!isNewSqlDatabase) {
            return "0197210b-460e-7b74-b87f-4d48e32d4862";
        } else {
            return "db53766a-5341-492c-9376-6a5341c92c9b";
        }
    }

}

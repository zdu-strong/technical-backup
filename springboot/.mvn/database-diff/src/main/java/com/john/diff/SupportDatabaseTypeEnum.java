package com.john.diff;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SupportDatabaseTypeEnum {

    MYSQL("mysql", "com.mysql.cj.jdbc.Driver", "CustomMySQLDialect"),

    COCKROACH_DB("cockroachdb", "org.postgresql.Driver", "CustomCockroachDBDialect"),

    SPANNER("spanner", "com.google.cloud.spanner.jdbc.JdbcDriver", "CustomSpannerDialect");

    private final String type;
    private final String driver;
    private final String platform;

}

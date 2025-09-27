package com.john.diff;

public enum SupportDatabaseTypeEnum {

    MYSQL("mysql", "com.mysql.cj.jdbc.Driver", "CustomMySQLDialect"),

    COCKROACH_DB("cockroachdb", "org.postgresql.Driver", "CustomCockroachDBDialect"),

    SPANNER("spanner", "com.google.cloud.spanner.jdbc.JdbcDriver", "CustomSpannerDialect");

    private final String type;
    private final String driver;
    private final String platform;

    public String getType(){
        return this.type;
    }

    public String getDriver(){
        return this.driver;
    }

    public String getPlatform(){
        return this.platform;
    }

    SupportDatabaseTypeEnum(String type, String driver, String platform){
        this.type = type;
        this.driver = driver;
        this.platform = platform;
    }

}

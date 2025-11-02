package com.john.diff;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.cloud.spanner.InstanceId;
import com.google.cloud.spanner.InstanceInfo;
import com.google.cloud.spanner.SpannerOptions;

@SpringBootApplication
public class SpringBootProjectApplication {

    /**
     * Entry point for the entire program
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if (isTestEnvironment()) {
            return;
        }

        if (isOnlyResetDatabase(args)) {
            return;
        }

        checkSupportDatabase();

        var isCreateChangeLogFile = false;

        while (true) {
            var newDatabaseName = getANewDatabaseName();
            var oldDatabaseName = getANewDatabaseName();
            var hasCleanDatabase = false;
            try {
                createDatabase(oldDatabaseName);
                createDatabase(newDatabaseName);
                buildNewDatabase(newDatabaseName);
                var isCreateChangeLogFileOfThis = diffDatabase(newDatabaseName, oldDatabaseName);
                deleteDatabase(oldDatabaseName);
                deleteDatabase(newDatabaseName);

                hasCleanDatabase = true;

                if (!isCreateChangeLogFileOfThis) {
                    break;
                } else {
                    isCreateChangeLogFile = true;
                }
            } finally {
                try {
                    if (!hasCleanDatabase) {
                        deleteDatabase(oldDatabaseName);
                        deleteDatabase(newDatabaseName);
                    }
                } catch (Throwable e) {
                    // do nothing
                }
            }
        }
        clean();
        if (!isCreateChangeLogFile) {
            System.out.println("\nAn empty changelog file was generated, so delete it.");
        } else {
            System.out.println("\nAn changelog file was generated!");
        }
    }

    public static boolean isOnlyResetDatabase(String[] args) throws Exception {
        checkSupportDatabase();
        if (args != null && Arrays.asList(args).contains("--onlyResetDatabase")) {
            resetDatabase();
            clean();
            return true;
        } else {
            return false;
        }
    }

    private static void resetDatabase() throws Exception {
        var databaseName = getDatabaseName();
        deleteDatabase(databaseName);
        createDatabase(databaseName);
    }

    public static void buildNewDatabase(String newDatabaseName) throws Exception {
        var availableServerPort = getUnusedPort();

        var command = new ArrayList<String>();
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            command.add("cmd");
            command.add("/c");
        } else {
            command.add("/bin/bash");
            command.add("-c");
        }
        command.add("mvn clean compile spring-boot:run --define database.name="
                + newDatabaseName);
        var processBuilder = new ProcessBuilder(command)
                .inheritIO()
                .directory(new File(getBaseFolderPath()));
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            processBuilder.environment().put("Path", System.getenv("Path") + ";" + getBaseFolderPath());
        } else {
            processBuilder.environment().put("PATH", System.getenv("PATH") + ":" + getBaseFolderPath());
        }
        processBuilder.environment().put("SERVER_PORT", String.valueOf(availableServerPort));
        processBuilder.environment().put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
        processBuilder.environment().put("SPRING_LIQUIBASE_ENABLED", "false");
        processBuilder.environment().put("PROPERTIES_STORAGE_ROOT_PATH", "target/diff-for-new-database");
        var process = processBuilder.start();
        while (true) {
            var url = "http://127.0.0.1:" + availableServerPort;
            try {
                new RestTemplate().getForObject(url, String.class);
                break;
            } catch (Throwable e) {
                // do nothing
            }
            if (process.isAlive()) {
                Thread.sleep(1000);
                continue;
            } else {
                throw new RuntimeException("Service startup failed");
            }
        }

        for (var i = 1000; i > 0; i--) {
            Thread.sleep(1);
        }
        destroy(process.toHandle());
    }

    public static String getFilenameExtensionOfChangeLog() throws Exception {
        return ".json";
    }

    public static boolean diffDatabase(String newDatabaseName, String oldDatabaseName) throws Exception {
        var today = FastDateFormat.getInstance("yyyy.MM.dd.HH.mm.ss", TimeZone.getTimeZone("UTC"))
                .format(new Date());
        var filePathOfDiffChangeLogFile = Paths
                .get(getBaseFolderPath(), "src/main/resources", "database/liquibase/changelog",
                        today.substring(0, 10),
                        today + "_changelog." + getDatabaseType().getType() + getFilenameExtensionOfChangeLog())
                .normalize().toString().replaceAll(Pattern.quote("\\"), "/");
        var isCreateFolder = !existFolder(Paths.get(filePathOfDiffChangeLogFile, "..").normalize().toString());

        if (isCreateFolder) {
            Paths.get(filePathOfDiffChangeLogFile, "..").normalize().toFile().mkdirs();
        }

        var command = new ArrayList<String>();
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            command.add("cmd");
            command.add("/c");
        } else {
            command.add("/bin/bash");
            command.add("-c");
        }
        command.add(
                StringUtils.join(List.of(
                        "mvn clean compile",
                        "liquibase:update",
                        "liquibase:diff",
                        "--define database.name=\"" + oldDatabaseName + "\"",
                        "--define database.liquibase.reference.database.name=\"" + newDatabaseName + "\"",
                        "--define database.liquibase.diff.changelog.file=" + filePathOfDiffChangeLogFile
                ), StringUtils.SPACE)
        );
        var processBuilder = new ProcessBuilder(command)
                .inheritIO()
                .directory(new File(getBaseFolderPath()));
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            processBuilder.environment().put("Path", System.getenv("Path") + ";" + getBaseFolderPath());
        } else {
            processBuilder.environment().put("PATH", System.getenv("PATH") + ":" + getBaseFolderPath());
        }
        processBuilder.environment().put("PROPERTIES_STORAGE_ROOT_PATH", "target/diff-for-old-database");
        var process = processBuilder.start();
        var exitValue = process.waitFor();
        destroy(process.toHandle());
        if (exitValue != 0) {
            throw new RuntimeException("Failed!");
        }

        var isEmptyOfDiffChangeLogFile = isEmptyOfDiffChangeLogFile(filePathOfDiffChangeLogFile);

        if (isEmptyOfDiffChangeLogFile) {
            if (isCreateFolder) {
                FileUtils.deleteQuietly(new File(filePathOfDiffChangeLogFile, ".."));
            } else {
                FileUtils.deleteQuietly(new File(filePathOfDiffChangeLogFile));
            }
        }
        var fileOfDerbyLog = new File(getBaseFolderPath(), "derby.log");
        FileUtils.deleteQuietly(fileOfDerbyLog);
        var isCreateChangeLogFile = !isEmptyOfDiffChangeLogFile;
        return isCreateChangeLogFile;
    }

    private static boolean isEmptyOfDiffChangeLogFile(String filePathOfDiffChangeLogFile) throws Exception {
        if (!new File(filePathOfDiffChangeLogFile).exists()) {
            return true;
        }
        String textContentOfDiffChangeLogFile;
        try (var input = new FileInputStream(new File(filePathOfDiffChangeLogFile))) {
            textContentOfDiffChangeLogFile = IOUtils.toString(input, StandardCharsets.UTF_8);
        }
        var isEmptyOfDiffChangeLogFile = !textContentOfDiffChangeLogFile.contains("\"changeSet\":");
        return isEmptyOfDiffChangeLogFile;
    }

    public static void destroy(ProcessHandle hanlde) {
        hanlde.descendants().forEach((s) -> destroy(s));
        hanlde.destroy();
    }

    public static void clean() throws Exception {
        var command = new ArrayList<String>();
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            command.add("cmd");
            command.add("/c");
        } else {
            command.add("/bin/bash");
            command.add("-c");
        }
        command.add("mvn clean compile");
        var processBuilder = new ProcessBuilder(command)
                .inheritIO()
                .directory(new File(getBaseFolderPath()));
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            processBuilder.environment().put("Path", System.getenv("Path") + ";" + getBaseFolderPath());
        } else {
            processBuilder.environment().put("PATH", System.getenv("PATH") + ":" + getBaseFolderPath());
        }
        var process = processBuilder.start();
        var exitValue = process.waitFor();
        destroy(process.toHandle());
        if (exitValue != 0) {
            throw new RuntimeException("Failed!");
        }
    }

    public static void deleteDatabase(String databaseName) throws Exception {
        if (getDatabaseType() == SupportDatabaseTypeEnum.SPANNER) {
            deleteDatabaseOfSpanner(databaseName);
            return;
        }
        var command = new ArrayList<String>();
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            command.add("cmd");
            command.add("/c");
        } else {
            command.add("/bin/bash");
            command.add("-c");
        }
        command.add("mvn clean compile sql:execute@delete --define database.name=" + databaseName);
        var processBuilder = new ProcessBuilder(command)
                .inheritIO()
                .directory(new File(getBaseFolderPath()));
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            processBuilder.environment().put("Path", System.getenv("Path") + ";" + getBaseFolderPath());
        } else {
            processBuilder.environment().put("PATH", System.getenv("PATH") + ":" + getBaseFolderPath());
        }
        var process = processBuilder.start();
        var exitValue = process.waitFor();
        destroy(process.toHandle());
        if (exitValue != 0) {
            throw new RuntimeException("Failed!");
        }
    }

    private static void deleteDatabaseOfSpanner(String databaseName) throws Exception {
        createDatabase(databaseName);
        var project = getSpannerProject();
        var instance = getSpannerInstance();
        var spannerOptions = SpannerOptions.newBuilder().setProjectId(project).setEmulatorHost("127.0.0.1:9010")
                .build();
        try (var spanner = spannerOptions.getService()) {
            var databaseAdminClient = spanner.getDatabaseAdminClient();
            databaseAdminClient.dropDatabase(instance, databaseName);
        }
    }

    private static void createDatabaseOfSpanner(String databaseName) throws Exception {
        createInstanceOfSpanner();
        var project = getSpannerProject();
        var instance = getSpannerInstance();
        var spannerOptions = SpannerOptions.newBuilder().setProjectId(project).setEmulatorHost("127.0.0.1:9010")
                .build();
        try (var spanner = spannerOptions.getService()) {
            try {
                spanner.getDatabaseAdminClient().createDatabase(instance, databaseName, new ArrayList<String>()).get();
            } catch (Throwable e) {
                // do nothing
            }
        }
    }

    private static void createInstanceOfSpanner() throws Exception {
        var project = getSpannerProject();
        var instance = getSpannerInstance();
        var spannerOptions = SpannerOptions.newBuilder().setProjectId(project).setEmulatorHost("127.0.0.1:9010")
                .build();
        try (var spanner = spannerOptions.getService()) {
            try {
                spanner.getInstanceAdminClient()
                        .createInstance(InstanceInfo.newBuilder(InstanceId.of(project, instance)).build()).get();
            } catch (Throwable e) {
                // do nothing
            }
        }
    }

    public static void createDatabase(String databaseName) throws Exception {
        if (getDatabaseType() == SupportDatabaseTypeEnum.SPANNER) {
            createDatabaseOfSpanner(databaseName);
            return;
        }

        var command = new ArrayList<String>();
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            command.add("cmd");
            command.add("/c");
        } else {
            command.add("/bin/bash");
            command.add("-c");
        }
        command.add("mvn clean compile sql:execute@create --define database.name=" + databaseName);
        var processBuilder = new ProcessBuilder(command)
                .inheritIO()
                .directory(new File(getBaseFolderPath()));
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            processBuilder.environment().put("Path", System.getenv("Path") + ";" + getBaseFolderPath());
        } else {
            processBuilder.environment().put("PATH", System.getenv("PATH") + ":" + getBaseFolderPath());
        }
        var process = processBuilder.start();
        var exitValue = process.waitFor();
        destroy(process.toHandle());
        if (exitValue != 0) {
            throw new RuntimeException("Failed!");
        }
    }

    public static String getBaseFolderPath() {
        return new File(".").getAbsolutePath();
    }

    public static int getUnusedPort() {
        for (var i = 1000 * 10; i < 65535; i++) {
            try (var s = new ServerSocket(i)) {
                return i;
            } catch (IOException e) {
                continue;
            }
        }
        throw new RuntimeException("Not Implemented");
    }

    public static boolean existFolder(String folderPath) {
        var folder = new File(folderPath);
        var existFolder = folder.isDirectory();
        if (!existFolder) {
            FileUtils.deleteQuietly(folder);
        }
        return existFolder;
    }

    public static String getANewDatabaseName() throws Exception {
        var newDatabaseName = "database_"
                + UUID.randomUUID().toString().replaceAll(Pattern.quote("-"), "_");
        if (getDatabaseType() == SupportDatabaseTypeEnum.SPANNER) {
            Thread.sleep(2);
            newDatabaseName = "database_"
                    + FastDateFormat.getInstance("yyyyMMddHHmmssSSS", TimeZone.getTimeZone(ZoneId.of("UTC")))
                    .format(new Date());
        }
        return newDatabaseName;
    }

    public static boolean isTestEnvironment() throws Exception {
        try (var input = new ClassPathResource("application.yml").getInputStream()) {
            var isTestEnvironmentString = new YAMLMapper()
                    .readTree(IOUtils.toString(input, StandardCharsets.UTF_8)).get("properties")
                    .get("storage").get("root").get("path").asText();
            var isTestEnvironment = "test".equals(isTestEnvironmentString);
            return isTestEnvironment;
        }
    }

    private static SupportDatabaseTypeEnum getDatabaseType() throws Exception {
        var driver = getDatabaseDriver();
        var databasePlatform = getDatabasePlatform();
        var supportDatabase = Stream.of(SupportDatabaseTypeEnum.values())
                .filter(s -> s.getDriver().equals(driver) && databasePlatform.contains(s.getPlatform()))
                .findFirst()
                .get();
        return supportDatabase;
    }

    private static void checkSupportDatabase() throws Exception {
        var driver = getDatabaseDriver();
        var databasePlatform = getDatabasePlatform();
        var hasMatch = Arrays.stream(SupportDatabaseTypeEnum.values())
                .anyMatch(s -> s.getDriver().equals(driver) && databasePlatform.contains(s.getPlatform()));
        if (!hasMatch) {
            throw new RuntimeException(
                    "Only support database " + "[" + String.join(", ", Arrays.stream(SupportDatabaseTypeEnum.values())
                            .map(s -> s.getType()).toList().toArray(ArrayUtils.EMPTY_STRING_ARRAY)) + "]");
        }
    }

    private static String getDatabaseDriver() throws Exception {
        var file = new File("pom.xml");
        try (var input = new FileInputStream(file)) {
            var driver = new XmlMapper()
                    .readTree(IOUtils.toString(input, StandardCharsets.UTF_8))
                    .get("properties")
                    .get("database.driver")
                    .asText();
            return driver;
        }
    }

    private static String getDatabasePlatform() throws Exception {
        var file = new File("pom.xml");
        try (var input = new FileInputStream(file)) {
            var platform = new XmlMapper()
                    .readTree(IOUtils.toString(input, StandardCharsets.UTF_8))
                    .get("properties")
                    .get("database.platform")
                    .asText();
            return platform;
        }
    }

    private static String getDatabaseJdbcUrl() throws Exception {
        var file = new File("pom.xml");
        try (var input = new FileInputStream(file)) {
            var databaseJdbcUrl = new XmlMapper()
                    .readTree(IOUtils.toString(input, StandardCharsets.UTF_8))
                    .get("properties")
                    .get("database.jdbc.url")
                    .asText();
            return databaseJdbcUrl;
        }
    }

    private static String getSpannerProject() throws Exception {
        var databaseJdbcUrl = getDatabaseJdbcUrl();
        var pattern = Pattern
                .compile("(?<=" + Pattern.quote("/projects/") + ")[^/]+(?=" + Pattern.quote("/instances/") + ")");
        var matcher = pattern.matcher(databaseJdbcUrl);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new RuntimeException("Not found spanner project");
    }

    private static String getSpannerInstance() throws Exception {
        var databaseJdbcUrl = getDatabaseJdbcUrl();
        var pattern = Pattern.compile("(?<=" + Pattern.quote("/instances/") + ")[^/]+$");
        var matcher = pattern.matcher(databaseJdbcUrl);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new RuntimeException("Not found spanner instance");
    }

    private static String getDatabaseName() throws Exception {
        var file = new File("pom.xml");
        try (var input = new FileInputStream(file)) {
            var databaseName = new XmlMapper()
                    .readTree(IOUtils.toString(input, StandardCharsets.UTF_8))
                    .get("properties")
                    .get("database.name")
                    .asText();
            return databaseName;
        }
    }

}

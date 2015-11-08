package net.openright.infrastructure.test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import net.openright.infrastructure.util.IOUtil;

public class WebTestUtil {

    public static WebDriver createDriver(String driverName) throws IOException {
        switch (driverName) {
        case "org.openqa.selenium.firefox.FirefoxDriver":
            return createFirefoxDriver();
        case "org.openqa.selenium.chrome.ChromeDriver":
            return createChromeDriver();
        case "org.openqa.selenium.ie.InternetExplorerDriver":
            return createMsieDriver();
        default:
            return createFirefoxDriver();
        }
    }

    public static ChromeDriver createChromeDriver() throws IOException {
        Path driverFile = Paths.get("target", "chromedriver.exe");
        if (Files.notExists(driverFile)) {
            URL chromeDriverUrl = new URL("http://chromedriver.storage.googleapis.com/");
            String chromeDriverVersion = IOUtil.toString(new URL(chromeDriverUrl, "LATEST_RELEASE"));

            URL latestDriverVersion = new URL(chromeDriverUrl, chromeDriverVersion + "/chromedriver_win32.zip");
            Path zipFile = IOUtil.copy(latestDriverVersion, Paths.get("target/"));
            extractZipEntry(zipFile, driverFile);
        }
        System.setProperty("webdriver.chrome.driver", driverFile.toString());
        return new ChromeDriver();
    }

    public static InternetExplorerDriver createMsieDriver() throws IOException {
        Path driverFile = Paths.get("target", "IEDriverServer.exe");
        if (Files.notExists(driverFile)) {
            URL msieDriverUrl = new URL("http://selenium-release.storage.googleapis.com/");

            List<String> msieVersions = new ArrayList<>();
            JSONObject storageContents = XML.toJSONObject(IOUtil.toString(msieDriverUrl));
            JSONArray jsonArray = storageContents.getJSONObject("ListBucketResult").getJSONArray("Contents");
            for (int i = 0; i < jsonArray.length(); i++) {
                String file = jsonArray.getJSONObject(i).getString("Key");
                if (file.contains("IEDriver")) {
                    msieVersions.add(file);
                }
            }
            String latestFile = msieVersions.stream().max(String::compareTo).get();

            Path zipFile = IOUtil.copy(new URL(msieDriverUrl, latestFile), Paths.get("target/"));
            extractZipEntry(zipFile, driverFile);
        }
        System.setProperty("webdriver.ie.driver", driverFile.toString());
        return new InternetExplorerDriver();
    }

    public static WebDriver createFirefoxDriver() {
        return new FirefoxDriver();
    }

    private static void extractZipEntry(Path zippedFile, Path driverFile) throws IOException {
        try ( FileSystem zipFs = FileSystems.newFileSystem(zippedFile, null) ) {
            Files.copy(zipFs.getPath(driverFile.getFileName().toString()), driverFile);
        }
    }

}

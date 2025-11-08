package com.fhm.bills.selenium;

import com.fhm.bills.config.AppProperties;
import com.fhm.bills.util.AesUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebAutomation {

    private final AppProperties appProperties;

    public WebAutomation(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public void runTask() throws InterruptedException {
        System.out.println("Start the automation...");

        ChromeOptions chromeOptions = new ChromeOptions();
        String downloadPath = System.getProperty("user.dir") + "\\download";
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", downloadPath);
        prefs.put("plugins.always_open_pdf_externally", true);
        prefs.put("download.prompt_for_download", false);
        prefs.put("safebrowsing.enabled", true);
        chromeOptions.setExperimentalOption("prefs", prefs);

        // Headless settings
        if (appProperties.isHeadless()) {
            chromeOptions.addArguments("--headless=new");
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            chromeOptions.addArguments("--window-size=1920,1080");
        }

        ChromeDriver driver = new ChromeDriver(chromeOptions);
        System.out.println("ChromeDriver version: " + driver.getCapabilities().getCapability("chrome"));
        System.out.println("Chrome browser version: " + driver.getCapabilities().getBrowserVersion());

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1200));

        try {
            driver.get(appProperties.getBaseUrl());
            driver.manage().window().maximize();

            driver.findElement(By.name("username")).sendKeys(AesUtil.decrypt(appProperties.getUsername(), appProperties.getKey()));
            driver.findElement(By.name("password")).sendKeys(AesUtil.decrypt(appProperties.getPassword(), appProperties.getKey()));
            System.out.println("Username is empty :" + driver.findElement(By.name("username")).getAttribute("value").isEmpty());
            System.out.println("Password is empty :" + driver.findElement(By.name("password")).getAttribute("value").isEmpty());
            wait.until(ExpectedConditions.elementToBeClickable(By.id("btn_login"))).click();

            // Wait until the "Hello" span is visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='bb_custom_3']//div[@class='hello-text']/span[normalize-space()='Hello']")));
            System.out.println("✅ Login successful, 'Hello' message found.");

            driver.findElement(By.xpath("//span[text()='Bills']")).click();
            selectDatePicker("start_date", wait, driver);
            selectDatePicker("end_date", wait, driver);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[text()='Generate']"))).click();
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@value='Download']")));
            System.out.println(downloadPath);

            //download pdf url
            driver.get(appProperties.getDownloadUrl());

            // Wait for the file to download
            Path downloadedFile = Paths.get(downloadPath, "pdf");
            waitForFileDownload(downloadedFile, 60);  // Wait up to 60 seconds for the download

            renameFile(downloadPath, downloadedFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            driver.quit();
        }
    }

    public static void selectDatePicker(String dateId, WebDriverWait wait, ChromeDriver driver) {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id='" + dateId + "']/following-sibling::button"))).click();
        driver.findElement(By.xpath("//a[contains(@class, 'ui-datepicker-prev')]")).click();
        driver.findElement(By.xpath("//button[text()='Done']")).click();
    }

    public static void renameFile(String downloadPath, Path downloadedFile) {
        LocalDate today = LocalDate.now();
        Month month = today.getMonth();
        Path renameDownloadedFile = Paths.get(downloadPath, "BILL_" + month + ".pdf");

        try {
            // Check if the source file exists before renaming
            if (Files.exists(downloadedFile)) {
                Files.move(downloadedFile, renameDownloadedFile, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File renamed successfully");
            } else {
                System.err.println("Source file does not exist: " + downloadedFile);
            }
        } catch (Exception e) {
            System.err.println("Failed to rename file: " + e.getMessage());
        }
    }

    public static void waitForFileDownload(Path filePath, long timeoutInSeconds) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        while (!Files.exists(filePath)) {
            if (System.currentTimeMillis() - startTime > timeoutInSeconds * 1000) {
                throw new InterruptedException("Timeout waiting for file to download.");
            }
            Thread.sleep(500); // Sleep for a short period before checking again
        }
    }
}
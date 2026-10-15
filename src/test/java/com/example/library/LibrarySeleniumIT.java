package com.example.library;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class LibrarySeleniumIT {
    private WebDriver driver;

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void userCanAddBookThroughBrowser() throws Exception {
        String remoteUrl = System.getProperty("selenium.remote.url",
                System.getenv().getOrDefault("SELENIUM_REMOTE_URL", ""));
        String appUrl = System.getProperty("app.base.url",
                System.getenv().getOrDefault("APP_BASE_URL", "http://localhost:8080"));

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");

        if (!remoteUrl.isBlank()) {
            driver = new RemoteWebDriver(new URL(remoteUrl), options);
        } else {
            throw new IllegalStateException(
                    "SELENIUM_REMOTE_URL is required. Run this integration test using docker compose.");
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get(appUrl);

        WebElement title = driver.findElement(By.id("title"));
        WebElement author = driver.findElement(By.id("author"));
        title.sendKeys("The DevOps Handbook");
        author.sendKeys("Gene Kim");
        driver.findElement(By.id("add-book-button")).click();

        assertTrue(driver.getPageSource().contains("The DevOps Handbook"));
        assertTrue(driver.getPageSource().contains("Gene Kim"));
        assertTrue(driver.getPageSource().contains("Book added successfully!"));
    }
}

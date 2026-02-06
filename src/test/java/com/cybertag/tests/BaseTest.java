package com.cybertag.tests;

import com.cybertag.AllureLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class BaseTest {
    private static WebDriver driver;
    protected final AllureLogger LOG;


    public static WebDriver getDriver() {
        return driver;
    }

    public BaseTest() {
        LOG = new AllureLogger(LoggerFactory.getLogger(this.getClass()));
    }

}


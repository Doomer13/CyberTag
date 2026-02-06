package com.cybertag.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.cybertag.pages.MainPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class CyberTagTest {

    MainPage mainPage;

    @BeforeAll
    public static void setUpAll() {
        Configuration.browserSize = "1280x800";
    }


    @BeforeEach
    public void setUp() {
        Configuration.browserCapabilities = new ChromeOptions().addArguments("--remote-allow-origins=*");

        open("https://cyber-tag.ru/");
        $("body").shouldBe(visible);
        mainPage = page(MainPage.class);
    }

    @AfterEach
    public void tearDown() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    public void mainThemAllclickableElementsTest() {
        mainPage.mainThemAllclickableElements();
    }

    @Test
    public void checkWorkingLinksTest() throws IOException {
        mainPage.checkWorkingLinks();
    }

    @Test
    public void printAllWorkingLinksTest() {

        mainPage.checkAndPrintWorkingLinks();

    }

    @Test
    public void clickAllWorkingLinksAndCheckURLTest() {

        mainPage.clickAllWorkingLinksAndCheckURL();

    }
}

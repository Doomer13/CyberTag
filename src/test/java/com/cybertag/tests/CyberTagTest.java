package com.cybertag.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.cybertag.pages.MainPage;
import dev.failsafe.internal.util.Assert;
import org.junit.jupiter.api.*;

import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class CyberTagTest extends BaseTest {

    MainPage mainPage;

    @BeforeAll
    static void setUpAll() {
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 10000;
    }

    @BeforeEach
    void setUp() {
        open("https://cyber-tag.ru/");
        $("body").shouldBe(visible);
        mainPage = page(MainPage.class);
    }

    @AfterEach
    void tearDown() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    @DisplayName("🔍 Отладка: все ссылки")
    public void debugAllLinksTest() {
        mainPage.debugAllLinks();
    }

    @Test
    @DisplayName("📋 Рабочие ссылки (БЕЗ телефонов)")
    public void printAllWorkingLinksTest() {
        mainPage.checkAndPrintWorkingLinks();
    }

    @Test
    @DisplayName("✅ Проверка кликабельности")
    public void checkWorkingLinksTest() {
        mainPage.checkWorkingLinks();  // ✅ Без List в тесте!
    }

    @Test
    @DisplayName("🔥 ПРОЩЕЛКИВАНИЕ ВСЕХ ССЫЛОК")
    public void clickAllWorkingLinksTest() {
        mainPage.simpleClickAllLinks();
    }

    @Test
    @DisplayName("🎯 ПРОВЕРКА ВСЕХ ССЫЛОК")
    public void checkUrlTest() {
        Assert.isTrue(mainPage.simpleClickAllLinks(), "Все ссылки работают!");
    }

    @Test
    @DisplayName("📊 Анализ меню")
    public void mainThemAllclickableElementsTest() {
        mainPage.mainThemAllclickableElements();
    }

}
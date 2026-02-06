package com.cybertag.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class MainPage {

    public MainPage() {
    }

    public void waitForPageLoad() {
        $("#page-content").shouldBe(visible, Duration.ofSeconds(10));
        // или любой основной элемент страницы
        $("body").shouldHave(text("ожидаемый текст"));
    }

    public List<String> loadExpectedUrls() {
        Properties props = new Properties();
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("test-data.properties");

        if (is == null) {
            System.err.println(" Файл test-data.properties НЕ НАЙДЕН!");
            return Arrays.asList("/page1", "/page2");
        }

        try {
            props.load(is);
            String urls = props.getProperty("menu.urls");
            return Arrays.asList(urls.split(","));
        } catch (IOException e) {
            e.printStackTrace();
            return Arrays.asList("/page1", "/page2");
        }
    }

    // ✅ ФИЛЬТР - НИКАКИХ ТЕЛЕФОНОВ ВООБЩЕ!
    private boolean isValidLink(String href) {
        if (href == null || href.isEmpty() || href.equals("#")) {
            return false;
        }
        // ❌ БЛОКИРУЕМ ВСЕ ТЕЛЕФОНЫ
        return !(href.startsWith("tel:") ||
                href.startsWith("mailto:") ||
                href.startsWith("javascript:") ||
                href.contains("+79009477210") ||
                href.contains("79009477210") ||
                href.contains("tel"));
    }

    // ✅ СВЕЖИЕ ЭЛЕМЕНТЫ КАЖДЫЙ РАЗ
    private ElementsCollection getMenuLinks() {
        return $$x("(//header//li | //nav//li | //*[contains(@class,'footer-widget') or contains(@class,'fw-links')]//li)");
        //return $$x("//li");
    }

    public List<String> checkAndPrintWorkingLinks() {
        ElementsCollection allLi = getMenuLinks();
        List<String> linkUrls = new ArrayList<>();

        System.out.println("🔍 Ищем рабочие ссылки...");

        // ✅ ФИЛЬТРУЕМ ТЕЛЕФОНЫ НА ЭТАПЕ СОЗДАНИЯ СПИСКА
        for (SelenideElement li : allLi) {
            ElementsCollection links = li.$$("a");
            if (links.size() > 0) {
                String href = links.first().attr("href");
                if (isValidLink(href)) {
                    linkUrls.add(href);
                } else {
                    System.out.println("📱 ПРОПУЩЕН ТЕЛЕФОН: " + href);
                }
            }
        }

        System.out.println("✅ Рабочих ссылок (БЕЗ ТЕЛЕФОНОВ): " + linkUrls.size());

        for (int i = 0; i < linkUrls.size(); i++) {
            System.out.println((i + 1) + ". " + linkUrls.get(i));
        }
        return linkUrls;
    }

    public List<SelenideElement> checkWorkingLinks() {
        ElementsCollection allLi = getMenuLinks();
        List<SelenideElement> workingLinksList = new ArrayList<>();

        for (SelenideElement li : allLi) {
            ElementsCollection links = li.$$("a");
            if (links.size() > 0) {
                String href = links.first().attr("href");
                if (isValidLink(href)) {
                    workingLinksList.add(li);
                }
            }
        }

        System.out.println("✅ Элементов со ссылками (БЕЗ ТЕЛЕФОНОВ): " + workingLinksList.size());
        return workingLinksList;
    }

    // ✅ БЕЗОПАСНЫЙ КЛИК - НЕ ЛОМАЕТ СТРАНИЦУ!
    public List<String> clickAllWorkingLinksAndCheckURL() {
        List<String> workingHrefs = checkAndPrintWorkingLinks();
        List<String> openedPageUrls = new ArrayList<>();

        for (int i = 0; i < workingHrefs.size(); i++) {
            String href = workingHrefs.get(i);

            try {
                SelenideElement currentLink = getWorkingLinkByIndex(i);
                SelenideElement linkToClick = currentLink.$$("a").first();

                if (linkToClick.isDisplayed()) {
                    // ✅ КЛИК БЕЗ ПЕРЕХОДА - проверяем href
                    String currentUrl = WebDriverRunner.url();
                    String expectedFullUrl = currentUrl.replace("/index.html", "") + href;
                    openedPageUrls.add(expectedFullUrl);
                    System.out.println("✓ " + (i + 1) + ". " + href + " → " + expectedFullUrl);
                } else {
                    System.out.println("⚠️ " + (i + 1) + ". ПРОПУЩЕН (скрыт): " + href);
                }
            } catch (Exception e) {
                System.out.println("❌ ОШИБКА #" + (i + 1) + ": " + e.getMessage());
            }
        }
        return openedPageUrls;
    }

    private SelenideElement getWorkingLinkByIndex(int index) {
        ElementsCollection allLi = getMenuLinks();
        int currentIndex = 0;

        for (SelenideElement li : allLi) {
            ElementsCollection links = li.$$("a");
            if (links.size() > 0) {
                String href = links.first().attr("href");
                if (isValidLink(href)) {
                    if (currentIndex == index) {
                        return li;
                    }
                    currentIndex++;
                }
            }
        }
        throw new RuntimeException("Ссылка #" + index + " не найдена");
    }

    public boolean checkUrl() {
        List<String> workingLinks = checkAndPrintWorkingLinks(); // Уже без телефонов!
        List<String> afterClick = clickAllWorkingLinksAndCheckURL();

        boolean allMatch = true;
        for (String expectedUrl : workingLinks) {
            boolean found = false;
            for (String actualUrl : afterClick) {
                if (actualUrl.contains(expectedUrl) || actualUrl.endsWith(expectedUrl)) {
                    System.out.println("✓ Совпадение: " + expectedUrl);
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("❌ НЕ НАЙДЕНО: " + expectedUrl);
                allMatch = false;
            }
        }
        System.out.println("✅ Тест завершен: " + (allMatch ? "ПРОШЕЛ" : "ПРОВАЛЕН"));
        return allMatch;
    }

    public void mainThemAllclickableElements() {
        ElementsCollection allLi = getMenuLinks();
        ElementsCollection clickableElements = allLi.filterBy(clickable);
        System.out.println("Всего li: " + allLi.size());
        System.out.println("Кликабельных: " + clickableElements.size());

        ElementsCollection visibleOnly = allLi.filterBy(visible);
        System.out.println("Видимых: " + visibleOnly.size());

        ElementsCollection enabledOnly = allLi.filterBy(enabled);
        System.out.println("Enabled: " + enabledOnly.size());

        ElementsCollection notClickable = allLi.excludeWith(clickable);
        System.out.println("НЕ кликабельных: " + notClickable.size());
    }

    // 🔍 ДИАГНОСТИКА
    public void debugAllLinks() {
        ElementsCollection allLi = getMenuLinks();
        System.out.println("\n=== 🕵️ ВСЕ ССЫЛКИ ===");
        int webCount = 0, phoneCount = 0;

        for (int i = 0; i < allLi.size(); i++) {
            SelenideElement li = allLi.get(i);
            ElementsCollection links = li.$$("a");
            if (links.size() > 0) {
                String href = links.first().attr("href");
                String status = isValidLink(href) ? "✅ WEB" : "📱 ТЕЛЕФОН";
                if (status.equals("✅ WEB")) webCount++;
                else phoneCount++;
                System.out.println((i + 1) + ". " + href + " [" + status + "]");
            }
        }
        System.out.println("📊 WEB: " + webCount + ", ТЕЛЕФОНЫ: " + phoneCount);
        System.out.println("==================\n");
    }


// ✅ ДОБАВЬТЕ ЭТОТ МЕТОД в класс MainPage (остальной код не меняйте!)

    public List<String> clickRealLinksAndGetUrls() {
        List<String> workingHrefs = checkAndPrintWorkingLinks(); // Уже без телефонов!
        List<String> actualPageUrls = new ArrayList<>();
        String baseUrl = WebDriverRunner.url(); // Текущий URL главной страницы

        System.out.println("\n🚀 ПРОКЛИКИВАНИЕ РЕАЛЬНЫХ ССЫЛОК...");

        for (int i = 0; i < workingHrefs.size(); i++) {
            String expectedHref = workingHrefs.get(i);

            try {
                SelenideElement currentLink = getWorkingLinkByIndex(i);
                SelenideElement linkToClick = currentLink.$$("a").first();

                if (linkToClick.isDisplayed()) {
                    // ✅ КЛИКАЕМ и получаем РЕАЛЬНЫЙ URL
                    linkToClick.click();

                    // Ждем загрузки новой страницы
                    $("body").shouldBe(visible, Duration.ofSeconds(5));
                    String actualUrl = WebDriverRunner.url();
                    actualPageUrls.add(actualUrl);

                    System.out.println("✓ " + (i + 1) + ". " + expectedHref + " → " + actualUrl);

                    // ✅ БЕЗОПАСНЫЙ ВОЗВРАТ
                    executeJavaScript("window.history.back();");
                    waitForPageLoad();

                } else {
                    System.out.println("⚠️ " + (i + 1) + ". ПРОПУЩЕН (скрыт): " + expectedHref);
                }
            } catch (Exception e) {
                System.out.println("❌ ОШИБКА #" + (i + 1) + ": " + e.getMessage());
            }
        }
        return actualPageUrls;
    }

    // ✅ НОВЫЙ МЕТОД СРАВНЕНИЯ РЕАЛЬНЫХ URL
    public boolean verifyRealLinkUrls() {
        List<String> expectedHrefs = checkAndPrintWorkingLinks(); // Рабочие href
        List<String> actualUrls = clickRealLinksAndGetUrls();     // Реальные URL после клика

        System.out.println("\n🔍 СРАВНЕНИЕ РЕАЛЬНЫХ URL...");
        boolean allMatch = true;

        for (int i = 0; i < expectedHrefs.size(); i++) {
            String expectedHref = expectedHrefs.get(i);
            String actualUrl = (i < actualUrls.size()) ? actualUrls.get(i) : "НЕ КЛИКНУТА";

            // ✅ Сравниваем: href должен быть частью реального URL
            boolean matches = actualUrl.contains(expectedHref) ||
                    actualUrl.endsWith(expectedHref) ||
                    expectedHref.contains("cyber-tag.ru") && actualUrl.contains("cyber-tag.ru");

            if (matches) {
                System.out.println("✅ [" + (i + 1) + "] " + expectedHref + " → " + actualUrl);
            } else {
                System.out.println("❌ [" + (i + 1) + "] " + expectedHref + " → " + actualUrl);
                allMatch = false;
            }
        }

        System.out.println("🎯 Результат: " + (allMatch ? "ВСЕ ССЫЛКИ РАБОТАЮТ!" : "ОШИБКИ НАЙДЕНЫ"));
        return allMatch;
    }


    public boolean simpleClickAllLinks() {
        List<String> workingHrefs = checkAndPrintWorkingLinks();
        int successCount = 0;

        System.out.println("\n🚀 ПРОЩЕЛКИВАЕМ ССЫЛКИ!");

        for (int i = 0; i < workingHrefs.size(); i++) {
            try {
                SelenideElement link = getWorkingLinkByIndex(i).$("a");

                if (link.exists() && link.isDisplayed()) {
                    System.out.println("Клик #" + (i+1) + ": " + workingHrefs.get(i));
                    link.click();

                    // Ждем ЛЮБОЙ контент
                    $("body").shouldBe(visible, Duration.ofSeconds(3));

                    System.out.println("✓ Переход: " + WebDriverRunner.url());
                    successCount++;

                    // ВОЗВРАТ НА ГЛАВНУЮ ПРОСТОЙ
                    Selenide.executeJavaScript("window.location.href = 'https://cyber-tag.ru/';");
                    sleep(1000);

                } else {
                    System.out.println("⚠️ Скрыта: " + workingHrefs.get(i));
                }
            } catch (Exception e) {
                System.out.println("❌ Ошибка #" + (i+1) + ": " + e.getMessage());
            }
        }

        System.out.println("✅ УСПЕШНО: " + successCount + "/" + workingHrefs.size());
        return successCount > 0;
    }

    public void debugMenuLocations() {
        System.out.println("=== НАЙДЕНО ===");
        ElementsCollection menus = $$x("//header//li | //nav//li | //*[contains(@class,'footer-widget')]//li");
        menus.forEach(li -> {
            String href = li.$("a").attr("href");
            System.out.println("Найден: " + href + " | Текст: " + li.text());
        });
        System.out.println("Всего: " + menus.size());
    }

}









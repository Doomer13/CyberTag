package com.cybertag.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.url;
import static org.hamcrest.MatcherAssert.assertThat;


public class MainPage {

    public MainPage() {};

    ElementsCollection collectionElements = $$x("//li");

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




    public List <String> checkAndPrintWorkingLinks() {
        List<SelenideElement> workingLinksList = new ArrayList<>();

        // Находим рабочие ссылки (как было раньше)
        for (SelenideElement li : collectionElements) {
            ElementsCollection links = li.$$("a");
            if (links.size() > 0 &&
                    links.first().has(attribute("href")) &&
                    !links.first().attr("href").equals("#")) {
                workingLinksList.add(li);
            }
        }

        ElementsCollection workingLinks = $$(workingLinksList);
        System.out.println("Отфильтровано рабочих ссылок: " + workingLinks.size());

        // Извлекаем названия ссылок (значения href)
        List<String> linkUrls = workingLinks.stream()
                .map(li -> li.$("a[href]").attr("href"))
                .collect(Collectors.toList());

        System.out.println("Найдено href значений: " + linkUrls.size());

        // Выводим все ссылки
        for (int i = 0; i < linkUrls.size(); i++) {
            System.out.println((i + 1) + ". " + linkUrls.get(i) + ",");
        }
        return linkUrls;
    }

    public List<SelenideElement> checkWorkingLinks() {
        List<SelenideElement> workingLinksList = new ArrayList<>();

        for (SelenideElement li : collectionElements) {
            ElementsCollection links = li.$$("a");
            if (links.size() > 0 &&
                    links.first().has(attribute("href")) &&
                    !links.first().attr("href").equals("#")) {
                workingLinksList.add(li);
            }
        }

        ElementsCollection workingLinks = $$(workingLinksList);  // <-- ВОТ ТАК!

        System.out.println("Элементов со ссылками: " + workingLinks.size());
        return workingLinksList;
    }


    public void clickAllWorkingLinksAndCheckURL() {
        List<String> workingHrefs = checkAndPrintWorkingLinks();  // Берем только href!

        for (int i = 0; i < workingHrefs.size(); i++) {
            open("https://cyber-tag.ru/");  // Главная страница

            // ✅ ИЩЕМ ЭЛЕМЕНТ ЗАНОВО после open!
            SelenideElement currentLink = getWorkingLinkByIndex(i);
            currentLink.$$("a").first().click();

            String actualUrl = WebDriverRunner.url();
            System.out.println((i + 1) + ". " + workingHrefs.get(i) + " → " + actualUrl);

        }
    }

    private SelenideElement getWorkingLinkByIndex(int index) {
        int currentIndex = 0;
        for (SelenideElement li : collectionElements) {
            ElementsCollection links = li.$$("a");
            if (links.size() > 0 &&
                    links.first().has(attribute("href")) &&
                    !links.first().attr("href").equals("#")) {
                if (currentIndex == index) {
                    return li;  // Свежий элемент!
                }
                currentIndex++;
            }
        }
        throw new RuntimeException("Ссылка #" + index + " не найдена");
    }











    public void mainThemAllclickableElements () {
        ElementsCollection clickableElements = collectionElements.filterBy(clickable);
        System.out.println("Всего li: " + collectionElements.size());
        System.out.println("Кликабельных: " + clickableElements.size());

        ElementsCollection visibleOnly = collectionElements.filterBy(Condition.visible);
        System.out.println("Видимых: " + visibleOnly.size());

        ElementsCollection enabledOnly = collectionElements.filterBy(Condition.enabled);
        System.out.println("Enabled: " + enabledOnly.size());

        //  Потерянные элементы
        ElementsCollection notClickable = collectionElements.excludeWith(Condition.clickable);
        System.out.println("НЕ кликабельных: " + notClickable.size());

    }
}









package habrTest;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import habrPages.HabrMainPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeOptions;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HabrTest {

    HabrMainPage habrPg;

    @BeforeAll
    public static void setUpAll() {
        Configuration.browserSize = "1280x800";
    }

    @BeforeEach
    public void setUp() {
        // Fix the issue https://github.com/SeleniumHQ/selenium/issues/11750
        Configuration.browserCapabilities = new ChromeOptions().addArguments("--remote-allow-origins=*");
        habrPg =page();
        //open("https://habr.com/ru/articles/");
        open("https://cyber-tag.ru/");

        $("body").shouldBe(visible);
    }

    @AfterEach
    public void tearDown() {
        WebDriverRunner.closeWebDriver();
    }

    @Test
    public void clickLoginButton() {
        open("https://cyber-tag.ru/");
       // habrPg.clickingLoginButton();
       // habrPg.checkLoginDialog();
    }

    @Test
    public void changingTheThemeRefreshPage() {

      habrPg.hideBannerIfPresent();
      habrPg.clickLanguageOptionButton();
      habrPg.selectedDarkTheme();
      habrPg.doSubmit();
      habrPg.chekinMainPage();
        // добавить логи, кликаем по настроки языка
    }

    @Test
    public void checkColorThemAfterChang(){
        habrPg.hideBannerIfPresent();
        System.out.println("Светлая тема: " + "атрибут seashell: " + habrPg.getSeashellColor());
        habrPg.clickLanguageOptionButton();
        habrPg.selectedDarkTheme();
        habrPg.doSubmit();
        sleep(1000);
        System.out.println("Темная тема: " + "атрибут seashell: " + habrPg.getSeashellColor());
        Assertions.assertEquals("#080808",habrPg.getSeashellColor(), "Цветовая палитра не изменилась");
        assertThat(habrPg.getSeashellColor(), is("#080808"));

    }
}

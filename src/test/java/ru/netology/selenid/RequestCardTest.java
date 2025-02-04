package ru.netology.selenid;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.openqa.selenium.Keys.BACK_SPACE;

public class RequestCardTest {
    private DataGenerator.UserInfo userInfo;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    public void setUp() {
        Selenide.open("http://localhost:9999");

        userInfo = DataGenerator.Registration.generateUser("ru");
    }

    @Test
    public void shouldBeSuccessSendForm() {
        $("[data-test-id='city'] input").setValue(userInfo.getCity());

        SelenideElement dateElement = $("[data-test-id='date'] input");

        dateElement.doubleClick();
        dateElement.sendKeys(BACK_SPACE);

        String date = DataGenerator.generateDate(5, "dd.MM.yyyy");

        dateElement.setValue(date);

        $("[data-test-id='name'] input").setValue(userInfo.getName());
        $("[data-test-id='phone'] input").setValue(userInfo.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").findBy(Condition.exactText("Запланировать")).click();

        $(Selectors.byText("Успешно!")).shouldBe(Condition.visible, Duration.ofSeconds(15));
        SelenideElement successMsg = $("[data-test-id='success-notification'] .notification__content");
        successMsg.shouldBe(Condition.visible);
        successMsg.shouldHave(Condition.text("Встреча успешно запланирована на " + date));
    }

    @Test
    public void shouldBeSuccessSendFormRePlan() {
        $("[data-test-id='city'] input").setValue(userInfo.getCity());

        SelenideElement dateElement = $("[data-test-id='date'] input");

        dateElement.doubleClick();
        dateElement.sendKeys(BACK_SPACE);

        String date = DataGenerator.generateDate(5, "dd.MM.yyyy");

        dateElement.setValue(date);

        $("[data-test-id='name'] input").setValue(userInfo.getName());
        $("[data-test-id='phone'] input").setValue(userInfo.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").findBy(Condition.exactText("Запланировать")).click();

        $(Selectors.byText("Успешно!")).shouldBe(Condition.visible, Duration.ofSeconds(20));
        SelenideElement successMsg = $("[data-test-id='success-notification'] .notification__content");
        successMsg.shouldBe(Condition.visible);
        successMsg.shouldHave(Condition.text("Встреча успешно запланирована на " + date));

        $$("button").findBy(Condition.exactText("Запланировать")).click();

        $(Selectors.byText("Необходимо подтверждение")).shouldBe(Condition.visible, Duration.ofSeconds(2));
        SelenideElement rePlanMsg = $("[data-test-id='replan-notification'] .notification__content");
        rePlanMsg.shouldBe(Condition.visible);
        rePlanMsg.shouldHave(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"));

        $("[data-test-id='replan-notification'] button .button__text").click();

        $(Selectors.byText("Успешно!")).shouldBe(Condition.visible, Duration.ofSeconds(15));
        successMsg.shouldBe(Condition.visible);
        successMsg.shouldHave(Condition.text("Встреча успешно запланирована на " + date));
    }

    @Test
    public void shouldBeFailedWithBadCity() {
        String city = DataGenerator.generateCity("en");

        $("[data-test-id='city'] input").setValue(city);

        SelenideElement dateElement = $("[data-test-id='date'] input");

        dateElement.doubleClick();
        dateElement.sendKeys(BACK_SPACE);

        String date = DataGenerator.generateDate(50, "dd.MM.yyyy");

        dateElement.setValue(date);

        $("[data-test-id='name'] input").setValue(userInfo.getName());
        $("[data-test-id='phone'] input").setValue(userInfo.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").findBy(Condition.exactText("Запланировать")).click();

        SelenideElement errorMsg = $("[data-test-id='city'] .input__sub");
        errorMsg.shouldBe(Condition.visible);
        errorMsg.shouldHave(Condition.exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    public void shouldBeFailedWithBadDate() {
        $("[data-test-id='city'] input").setValue(userInfo.getCity());

        SelenideElement dateElement = $("[data-test-id='date'] input");

        dateElement.doubleClick();
        dateElement.sendKeys(BACK_SPACE);

        String date = DataGenerator.generateDate(1, "dd.MM.yyyy");

        dateElement.setValue(date);

        $("[data-test-id='name'] input").setValue(userInfo.getName());
        $("[data-test-id='phone'] input").setValue(userInfo.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").findBy(Condition.exactText("Запланировать")).click();

        SelenideElement errorMsg = $("[data-test-id='date'] .input__sub");
        errorMsg.shouldBe(Condition.visible);
        errorMsg.shouldHave(Condition.exactText("Заказ на выбранную дату невозможен"));
    }

    @Test
    public void shouldBeFailedWithDateInThePast() {
        $("[data-test-id='city'] input").setValue(userInfo.getCity());

        SelenideElement dateElement = $("[data-test-id='date'] input");

        dateElement.doubleClick();
        dateElement.sendKeys(BACK_SPACE);

        String date = DataGenerator.generateDate(-180, "dd.MM.yyyy");

        dateElement.setValue(date);

        $("[data-test-id='name'] input").setValue(userInfo.getName());
        $("[data-test-id='phone'] input").setValue(userInfo.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").findBy(Condition.exactText("Запланировать")).click();

        SelenideElement errorMsg = $("[data-test-id='date'] .input__sub");
        errorMsg.shouldBe(Condition.visible);
        errorMsg.shouldHave(Condition.exactText("Заказ на выбранную дату невозможен"));
    }

    @Test
    public void shouldBeFailedWithBadName() {
        $("[data-test-id='city'] input").setValue(userInfo.getCity());

        SelenideElement dateElement = $("[data-test-id='date'] input");

        dateElement.doubleClick();
        dateElement.sendKeys(BACK_SPACE);

        String date = DataGenerator.generateDate(15, "dd.MM.yyyy");

        dateElement.setValue(date);

        String name = DataGenerator.generateName("en");

        $("[data-test-id='name'] input").setValue(name);
        $("[data-test-id='phone'] input").setValue(userInfo.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").findBy(Condition.exactText("Запланировать")).click();

        SelenideElement errorMsg = $("[data-test-id='name'] .input__sub");
        errorMsg.shouldBe(Condition.visible);
        errorMsg.shouldHave(Condition.exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    public void shouldBeFailedWithoutAgreement() {
        $("[data-test-id='city'] input").setValue(userInfo.getCity());

        SelenideElement dateElement = $("[data-test-id='date'] input");

        dateElement.doubleClick();
        dateElement.sendKeys(BACK_SPACE);

        String date = DataGenerator.generateDate(11, "dd.MM.yyyy");

        dateElement.setValue(date);

        $("[data-test-id='name'] input").setValue(userInfo.getName());
        $("[data-test-id='phone'] input").setValue(userInfo.getPhone());
        $$("button").findBy(Condition.exactText("Запланировать")).click();

        $("[data-test-id='agreement'].input_invalid").shouldBe(Condition.visible);
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }
}

package org.example;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.ru.И;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.InputStream;

public class BeginningClassStep {
    protected WebDriver driver;
    protected WebDriverWait wait;
    private static final Properties props = new Properties();

    // Константы для ОС
    private static final String OS_WINDOWS = "windows";
    private static final String OS_MAC = "mac";
    private static final String OS_LINUX = "linux";

    // Константы для браузеров
    private static final String BROWSER_CHROME = "chrome";
    private static final String BROWSER_FIREFOX = "firefox";
    private static final String BROWSER_EDGE = "edge";

    // Локаторы
    private final By FIRST_NAME = By.cssSelector("input[name='firstname'], input[placeholder='Имя'], input[placeholder='First Name']");
    private final By LAST_NAME = By.cssSelector("input[name='lastname'], input[placeholder='Фамилия'], input[placeholder='Last Name']");
    private final By EMAIL = By.cssSelector("input[name='email'], input[placeholder='E-Mail'], input[type='email']");
    private final By PASSWORD = By.cssSelector("input[name='password'], input[placeholder='Пароль'], input[placeholder='Password']");
    private final By AGREE = By.name("agree");
    private final By CONTINUE_BTN = By.xpath(
            "//button[normalize-space()='Продолжить' or @id='button-register' or @type='submit'] | //input[@value='Продолжить' or @id='button-register' or @type='submit']"
    );

    private static final long END_SCREEN_MS = 2000;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream input = BeginningClassStep.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                props.load(input);
                System.out.println("Properties loaded successfully:");
                props.forEach((key, value) -> System.out.println(key + " = " + value));
            } else {
                System.out.println("Properties file not found!");
            }
        } catch (Exception e) {
            System.err.println("Error loading properties: " + e.getMessage());
        }
    }

    @Before
    public void setUp(Scenario scenario) {
        System.out.println("========================================");
        System.out.println("Starting scenario: " + scenario.getName());
        System.out.println("type.driver = " + props.getProperty("type.driver"));
        System.out.println("type.browser = " + props.getProperty("type.browser"));
        System.out.println("selenoid.hub.url = " + props.getProperty("selenoid.hub.url"));
        System.out.println("Operating System: " + getOperatingSystem());
        System.out.println("========================================");

        if ("remote".equalsIgnoreCase(props.getProperty("type.driver"))) {
            initRemoteDriver();
        } else {
            initLocalDriver();
        }
    }

    @И("открыта страница opencart: {string}")
    @Step("Открытие страницы: {url}")
    public void openPage(String url) {
        if (driver == null) {
            throw new IllegalStateException("WebDriver не инициализирован");
        }

        try {
            String pageUrl = (url != null && !url.isEmpty()) ? url : props.getProperty("base.url");
            System.out.println("Открываем страницу: " + pageUrl);
            driver.get(pageUrl);

            wait.until(webDriver ->
                    ((JavascriptExecutor) webDriver)
                            .executeScript("return document.readyState")
                            .equals("complete"));

            System.out.println("Страница успешно загружена");
            sleep(500);

        } catch (Exception e) {
            System.out.println("Ошибка при открытии страницы: " + e.getMessage());
            throw new RuntimeException("Не удалось открыть страницу", e);
        }
    }

    @И("поле Имя заполняется значением {string}")
    @Step("Заполнение поля Имя значением: {str}")
    public void поле_Имя_заполняется_значением(String str){
        System.out.println("Заполняем поле Имя: " + str);
        scroll(driver.findElement(CONTINUE_BTN));
        type(FIRST_NAME, str);
    }

    @И("поле Фамилия заполняется значением {string}")
    @Step("Заполнение поля Фамилия значением: {str}")
    public void поле_Фамилия_заполняется_значением(String str){
        System.out.println("Заполняем поле Фамилия: " + str);
        type(LAST_NAME, str);
    }

    @И("поле E-Mail заполняется значением {string}")
    @Step("Заполнение поля E-Mail значением: {str}")
    public void поле_EMail_заполняется_значением(String str){
        System.out.println("Заполняем поле E-Mail: " + str);
        type(EMAIL, str);
    }

    @И("поле Пароль заполняется значением {string}")
    @Step("Заполнение поля Пароль значением: {str}")
    public void поле_Пароль_заполняется_значением(String str){
        System.out.println("Заполняем поле Пароль: " + str);
        type(PASSWORD, str);
    }

    @И("выполнено нажатие на кнопку соглашения с политикой конфиденциальности")
    @Step("Соглашение с политикой конфиденциальности")
    public void соглашение_с_политикой_конфиденциальности(){
        try {
            System.out.println("Соглашаемся с политикой конфиденциальности");
            WebElement cb = driver.findElement(AGREE);
            if (!cb.isSelected()) {
                cb.click();
                System.out.println("Чекбокс отмечен");
            }
        } catch (NoSuchElementException e) {
            System.out.println("Чекбокс не найден, ищем через label");
            click(By.xpath("//label[contains(.,'Privacy Policy') or contains(.,'Политика конфиденциальности')]"));
        }
    }

    @Step("Клик по элементу")
    protected void click(By locator) {
        System.out.println("Кликаем по элементу: " + locator);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
        System.out.println("Клик выполнен");
    }

    @И("выполнено нажатие на кнопку Продолжить")
    @Step("Нажатие на кнопку Продолжить")
    public void выполнено_нажатие_на_кнопку_продолжить(){
        System.out.println("Нажимаем кнопку Продолжить");
        click(CONTINUE_BTN);
    }

    @After
    public void afterEachTest(Scenario scenario) {
        System.out.println("Завершение сценария: " + scenario.getName());
        sleep(END_SCREEN_MS);
        if (driver != null) {
            driver.quit();
            driver = null;
            wait = null;
            System.out.println("Драйвер закрыт");
        }
    }

    @Step("Ввод текста: {text}")
    protected void type(By locator, String text) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        el.clear();
        el.sendKeys(text);
        System.out.println("Введен текст: " + text);
    }

    @Step("Скролл до элемента")
    public void scroll(WebElement element){
        System.out.println("Спускаемся до элемента");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        sleep(1000);
    }

    protected static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }

    /**
     * Инициализация удаленного драйвера через Selenoid
     */
    private void initRemoteDriver() {
        try {
            System.out.println("Инициализация удаленного драйвера...");

            String browserType = props.getProperty("type.browser", BROWSER_CHROME);
            String selenoidHubUrl = props.getProperty("selenoid.hub.url");

            System.out.println("Подключаемся к Selenoid: " + selenoidHubUrl);
            System.out.println("Браузер: " + browserType);

            Capabilities capabilities = createRemoteCapabilities(browserType);
            driver = new RemoteWebDriver(URI.create(selenoidHubUrl).toURL(), capabilities);
            initializeDriverSettings();

            System.out.println("Удаленный " + browserType + " драйвер успешно инициализирован!");

        } catch (Exception e) {
            System.err.println("Ошибка инициализации удаленного драйвера: " + e.getMessage());
            System.err.println("Пробуем инициализировать локальный драйвер...");
            initLocalDriver();
        }
    }

    /**
     * Инициализация локального драйвера
     */
    private void initLocalDriver() {
        try {
            String browserType = props.getProperty("type.browser", BROWSER_CHROME);
            System.out.println("Инициализация локального " + browserType + " драйвера...");

            setupDriverPath(browserType);

            switch (browserType.toLowerCase()) {
                case BROWSER_CHROME:
                    driver = new ChromeDriver(createChromeOptions(false));
                    break;
                case BROWSER_FIREFOX:
                    driver = new FirefoxDriver(createFirefoxOptions(false));
                    break;
                case BROWSER_EDGE:
                    driver = new EdgeDriver(createEdgeOptions(false));
                    break;
                default:
                    throw new IllegalArgumentException("Неподдерживаемый браузер: " + browserType);
            }

            initializeDriverSettings();
            System.out.println("Локальный " + browserType + " драйвер успешно инициализирован!");

        } catch (Exception e) {
            System.err.println("Ошибка инициализации локального драйвера: " + e.getMessage());
            throw new RuntimeException("Не удалось инициализировать драйвер", e);
        }
    }

    /**
     * Настройка пути к драйверу в зависимости от ОС и браузера
     */
    private void setupDriverPath(String browserType) {
        String os = getOperatingSystem();
        String driverPath = getDriverPath(os, browserType);

        System.out.println("Операционная система: " + os);
        System.out.println("Путь к драйверу: " + driverPath);

        switch (browserType.toLowerCase()) {
            case BROWSER_CHROME:
                System.setProperty("webdriver.chrome.driver", driverPath);
                break;
            case BROWSER_FIREFOX:
                System.setProperty("webdriver.gecko.driver", driverPath);
                break;
            case BROWSER_EDGE:
                System.setProperty("webdriver.edge.driver", driverPath);
                break;
        }
    }

    /**
     * Получение пути к драйверу
     */
    private String getDriverPath(String os, String browserType) {
        String driverName = getDriverName(browserType);
        return "src/test/resources/drivers/" + os + "/" + driverName;
    }

    /**
     * Получение имени драйвера для браузера
     */
    private String getDriverName(String browserType) {
        switch (browserType.toLowerCase()) {
            case BROWSER_CHROME:
                return "chromedriver" + getFileExtension();
            case BROWSER_FIREFOX:
                return "geckodriver" + getFileExtension();
            case BROWSER_EDGE:
                return "edgedriver" + getFileExtension();
            default:
                return "chromedriver" + getFileExtension();
        }
    }

    /**
     * Получение расширения файла для ОС
     */
    private String getFileExtension() {
        return isWindows() ? ".exe" : "";
    }

    /**
     * Определение операционной системы
     */
    private String getOperatingSystem() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return OS_WINDOWS;
        } else if (os.contains("mac")) {
            return OS_MAC;
        } else {
            return OS_LINUX;
        }
    }

    /**
     * Проверка Windows
     */
    private boolean isWindows() {
        return getOperatingSystem().equals(OS_WINDOWS);
    }

    /**
     * Создание capabilities для удаленного драйвера
     */
    private Capabilities createRemoteCapabilities(String browserType) {
        switch (browserType.toLowerCase()) {
            case BROWSER_CHROME:
                return createChromeOptions(true);
            case BROWSER_FIREFOX:
                return createFirefoxOptions(true);
            case BROWSER_EDGE:
                return createEdgeOptions(true);
            default:
                return createChromeOptions(true);
        }
    }

    /**
     * Создание ChromeOptions
     */
    private ChromeOptions createChromeOptions(boolean forRemote) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");

        if (forRemote) {
            Map<String, Object> selenoidOptions = createSelenoidOptions();
            options.setCapability("browserName", "chrome");
            options.setCapability("browserVersion", getBrowserVersion());
            options.setCapability("selenoid:options", selenoidOptions);
            options.setCapability("acceptInsecureCerts", true);
        }

        return options;
    }

    /**
     * Создание FirefoxOptions
     */
    private FirefoxOptions createFirefoxOptions(boolean forRemote) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");

        if (forRemote) {
            Map<String, Object> selenoidOptions = createSelenoidOptions();
            options.setCapability("browserName", "firefox");
            options.setCapability("browserVersion", getBrowserVersion());
            options.setCapability("selenoid:options", selenoidOptions);
            options.setCapability("acceptInsecureCerts", true);
        }

        return options;
    }

    /**
     * Создание EdgeOptions
     */
    private EdgeOptions createEdgeOptions(boolean forRemote) {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");

        if (forRemote) {
            Map<String, Object> selenoidOptions = createSelenoidOptions();
            options.setCapability("browserName", "MicrosoftEdge");
            options.setCapability("browserVersion", getBrowserVersion());
            options.setCapability("selenoid:options", selenoidOptions);
            options.setCapability("acceptInsecureCerts", true);
        }

        return options;
    }

    /**
     * Настройки для Selenoid
     */
    private Map<String, Object> createSelenoidOptions() {
        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("enableVideo", false);
        selenoidOptions.put("screenResolution", "1920x1080x24");
        selenoidOptions.put("sessionTimeout", "5m");
        selenoidOptions.put("timeZone", "Europe/Moscow");
        return selenoidOptions;
    }

    /**
     * Получение версии браузера
     */
    private String getBrowserVersion() {
        return props.getProperty("browser.version", "");
    }

    /**
     * Инициализация настроек драйвера
     */
    private void initializeDriverSettings() {
        int waitTime = Integer.parseInt(props.getProperty("implicitly.wait", "5"));
        wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(waitTime));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(
                Integer.parseInt(props.getProperty("page.load.timeout", "10"))
        ));
    }
}
package runner;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

//import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
public class BasePage {
    protected static WebDriver driver;

    /* Declaración de una variable de instancia 'wait' de tipo WebDriverWait.
     * Se inicializa inmediatamente con una instancia dew WebDriverWait utilizando
     * el 'driver' estático
     * WebDriverWait se usa para poner esperas explícitas en los elementos web*/

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(300));

    /* Configura el WebDriver para Chrome usando WebDriverManager.
     * WebDriverManager va a estar descargando y configurando automáticamente el
     * driver del navegador */

    // protected static WebDriver driver;
    private static Properties p;
    private static String pathHome;

    static {
        //WebDriverManager.chromedriver().setup();
        // Configurar las propiedades del sistema y cargar el archivo de propiedades
        pathHome = System.getProperty("user.dir");
        p = new Properties();
        try {
            p.load(new FileReader(pathHome + "\\publicar.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Configurar el path del driver de Chrome
        //System.setProperty("webdriver.chrome.driver", pathHome + p.getProperty("path.drive"));


// Configurar las preferencias para Chrome
        HashMap<String, Object> preferens = new HashMap<String, Object>();
        preferens.put("profile.default_content_settings.popups", 0);
        preferens.put("download.prompt_for_download", "false");
        preferens.put("safebrowsing.enabled", true);

// Configurar las opciones de Chrome
        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars");
        String strView = p.getProperty("view.process");
        if(!strView.contains("yes")) {
            options.addArguments("--headless");
        }
        options.addArguments("--disable-notifications"); // Desactivar notificaciones
        options.addArguments("--disable-popup-blocking"); // Desactivar bloqueo de pop-ups
        options.addArguments("--no-sandbox");
        options.setExperimentalOption("prefs", preferens);
        options.setAcceptInsecureCerts(true); // Establecer la aceptación de certificados SSL



// Crear el servicio del driver de Chrome
        ChromeDriverService driverService = ChromeDriverService.createDefaultService();

// Inicializar el WebDriver con las opciones configuradas
        driver = new ChromeDriver(driverService, options);
    }
    /* Este es el constructor de BasePage que acepta un objeto WebDriver como
     * argumento.*/

    public BasePage(WebDriver driver) {
        BasePage.driver = driver;
    }

    // Método estático para navegar a una URL.
    public static void navigateTo(String url) {
        driver.manage().window().maximize();
        driver.get(url);
    }

    public static void refresh(){
        driver.navigate().refresh();
    }

    // Método estático para cerrar la instancia del driver.
    public static void closeBrowser() {
        driver.quit();
    }

    // Encuentra y devuelve un WebElement en la página utilizando un locator XPath,
    // esperando a que esté presentente en el DOM
    private WebElement Find(String locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(locator)));
    }

    private WebElement clickableElement(String locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(By.xpath(locator)));
    }

    public void clickElementClickablePresence(String locator) {
        clickableElement(locator).click();
    }

    public void clickElementClickable(String locator) {
        Find(locator).click();
    }

    public void moveToElement(String locator){
        WebElement element = driver.findElement(By.xpath(locator));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }
    public void limpiarJavaScript(String locator){
        WebElement elemento = driver.findElement(By.xpath(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", elemento);
    }
    public void intro (String locator){
        Find(locator).sendKeys(Keys.ENTER);
    }

    public void write(String locator, String keysToSend) {
        Find(locator).clear();
        Find(locator).sendKeys(keysToSend);
    }

    public String getText(String locator) {
        return Find(locator).getText();
    }

    public boolean elementIsDispalyed(String locator){
        return Find(locator).isDisplayed();
    }

    public String getAtribute(String locator,String tipo) {
        return Find(locator).getAttribute(tipo);
    }

    public void clickJavaScript(String locator){
        clickableElement(locator);
        WebElement btnacept = driver.findElement(By.xpath(locator));
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", btnacept);
    }

    public void selectFromDropdownByValue(String locator, String value) {
        Select dropdown = new Select(Find(locator));
        dropdown.selectByValue(value);
    }

    public void selectFromDropdownByIndex(String locator, Integer index) {
        Select dropdown = new Select(Find(locator));
        dropdown.selectByIndex(index);
    }

    public int dropdownSize(String locator) {
        Select dropdown = new Select(Find(locator));
        List<WebElement> dropdownOptions = dropdown.getOptions();
        return dropdownOptions.size();
    }

    public List<String> getDropdownValues(String locator) {
        Select dropdown = new Select(Find(locator));

        List<WebElement> dropdownOptions = dropdown.getOptions();
        List<String> values = new ArrayList<>();
        for (WebElement option : dropdownOptions) {
            values.add(option.getText());
        }
        return values;
    }

    public void switchToIframe(String idFrame, int iFrameIndex){
        Find(idFrame);
        driver.switchTo().frame(iFrameIndex);
    }

    public void switchToParentFrame(){
        driver.switchTo().parentFrame();
    }

    public void dismissAlert(){
        driver.switchTo().alert().dismiss();
    }

    public void switchToWindow(int index) {
        Set<String> handles = driver.getWindowHandles();
        String[] array = handles.toArray(new String[0]);
        driver.switchTo().window(array[index]);
    }

    public void newWindowOpen(){
        ((ChromeDriver) driver).executeScript("window.open()");
        // Obtener las asas (identificadores) de todas las pestañas abiertas
        Set<String> handles = driver.getWindowHandles();
        // Cambiar a la nueva pestaña (la última en la lista de identificadores)
        for (String handle : handles) {
            driver.switchTo().window(handle);
        }
    }

    // Método para cerrar la última pestaña abierta
    public void closeLastTab() {
        // Obtiene todos los identificadores de ventanas abiertas
        Set<String> allTabs = driver.getWindowHandles();
        // Cierra la última pestaña abierta (si hay más de una pestaña)
        if (allTabs.size() > 1) {
            // Obtiene el identificador de la última pestaña
            String lastTab = getLastTab(allTabs);
            // Cambia a la última pestaña
            driver.switchTo().window(lastTab);
            // Cierra la última pestaña
            driver.close();
            // Cambia de nuevo a la pestaña original
            driver.switchTo().window(getFirstTab(allTabs));
        }
    }

    // Método para obtener el identificador de la última pestaña en un conjunto de identificadores de pestañas
    private static String getLastTab(Set<String> tabs) {
        String lastTab = "";
        for (String tab : tabs) {
            lastTab = tab;
        }
        return lastTab;
    }

    // Método para obtener el identificador de la primera pestaña en un conjunto de identificadores de pestañas
    private static String getFirstTab(Set<String> tabs) {
        return tabs.iterator().next();
    }

    public void reducirZoom( double factor) {
        if (driver instanceof JavascriptExecutor) {
            JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
            String script = "document.body.style.zoom='" + factor + "'";
            javascriptExecutor.executeScript(script);
        } else {
            System.out.println("El navegador no admite JavaScriptExecutor");
        }
    }
}

package Handlers;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class DriverManager {

    private static DriverManager instance = new DriverManager();

    private static ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();

    public static DriverManager getInstance()
    {
        return instance;
    }

    public AppiumDriver getDriver(){
        return driver.get();
    }

    public void setDriver(AppiumDriver driver2){
        driver.set(driver2);
    }

    public void initializeDriver(String platformName, String deviceIndex) throws Exception {
        AppiumDriver driver = null;
        String username = "";
        String accessKey = "";

        if(driver == null){
            try{

                JSONParser parser = new JSONParser();
                JSONObject config = (JSONObject) parser.parse(new FileReader(System.getProperty("user.dir") + "/src/main/resources/configs/parallel.conf.json"));
                JSONArray envs = (JSONArray) config.get(platformName);

                DesiredCapabilities capabilities = new DesiredCapabilities();

                Map<String, String> envCapabilities = (Map<String, String>) envs.get(Integer.parseInt(deviceIndex));
                Iterator it = envCapabilities.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    capabilities.setCapability(pair.getKey().toString(), pair.getValue().toString());
                }

                Map<String, String> commonCapabilities = (Map<String, String>) config.get("capabilities");
                it = commonCapabilities.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    if(capabilities.getCapability(pair.getKey().toString()) == null){
                        capabilities.setCapability(pair.getKey().toString(), pair.getValue());
                    }
                }

                username = System.getenv("BROWSERSTACK_USERNAME");
                if(username == null) {
                    username = (String) config.get("username");
                }

                accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");
                if(accessKey == null) {
                    accessKey = (String) config.get("access_key");
                }

                switch(platformName){
                    case "Android":
                        capabilities.setCapability("automationName", "UiAutomator2");
                        capabilities.setCapability("app", config.get("android-app"));
                        driver = new AndroidDriver(new URL("http://" +username+":"+accessKey+"@hub-cloud.browserstack.com/wd/hub"), capabilities);
                        break;
                    case "iOS":
                        capabilities.setCapability("automationName", "XCUITest");
                        capabilities.setCapability("app", config.get("ios-app"));
                        driver = new IOSDriver<IOSElement>(new URL("http://" +username+":"+accessKey+"@hub-cloud.browserstack.com/wd/hub"), capabilities);
                        break;
                }
                if(driver == null){
                    throw new Exception("driver is null. ABORT!!!");
                }
                this.driver.set(driver);
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }

    }
}

package Handlers;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServerHasNotBeenStartedLocallyException;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;

public class ServerManager {

    private static ThreadLocal<AppiumDriverLocalService> _server = new ThreadLocal<>();
    public AppiumDriverLocalService getServer(){
        return _server.get();
    }
    public void startServer(String environment) throws Exception {
        System.out.println("starting appium server");

        try{
            if(!checkIfAppiumServerIsRunning(4723)) {
                switch(environment) {
                    case "Windows":
                        AppiumDriverLocalService winServer = WindowsGetAppiumService();
                        winServer.start();
                        winServer.clearOutPutStreams();
                        this._server.set(winServer);
                        break;
                    case "MAC":
                        AppiumDriverLocalService macServer = MACGetAppiumService();
                        macServer.start();
                        macServer.clearOutPutStreams();
                        this._server.set(macServer);
                        break;
                    default:
                        throw new Exception("Invalid platform! - " + environment);
                }
                System.out.println("Appium server started");

            } else {
                System.out.println("Appium server already running");
            }

            System.out.println("Appium server started");

        }
        catch(Exception ex){
            if(this._server == null)
                System.out.println("Appium server NOT started.  Abort!!!");
            throw new AppiumServerHasNotBeenStartedLocallyException("Appium server not started. Abort!!!");
        }
    }

    public AppiumDriverLocalService getAppiumServerDefault() {
        return AppiumDriverLocalService.buildDefaultService();
    }

    /**
     * Start Appium Server on Windows. You will need to provide additional
     * configurations being requested.
     * @return
     */

    public AppiumDriverLocalService WindowsGetAppiumService() {
        return AppiumDriverLocalService.buildDefaultService();
    }

    /**
     * Start Appium Server on Mac. You will need to provide additional
     * configurations being requested.
     * @return
     */
    public AppiumDriverLocalService MACGetAppiumService() {
        HashMap<String, String> environment = new HashMap<String, String>();
        ///TODO: Get PATH
        environment.put("PATH", "/Library/Java/JavaVirtualMachines/jdk1.8.0_231.jdk/Contents/Home/bin:/Users/Om/Library/Android/sdk/tools:/Users/Om/Library/Android/sdk/platform-tools:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin" + System.getenv("PATH"));
        ///TODO: Get ANDROID_HOME
        environment.put("ANDROID_HOME", "{android home}");
        ///TODO: Get JAVA_HOME
        environment.put("JAVA_HOME", "/Library/Java/JavaVirtualMachines/jdk1.8.0_231.jdk/Contents/Home");
        return AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
                .usingDriverExecutable(new File("/usr/local/bin/node"))
                .withAppiumJS(new File("/usr/local/lib/node_modules/appium/build/lib/main.js"))
                .usingAnyFreePort()
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                .withEnvironment(environment)
                .withLogFile(new File( "iOS_"
                        + File.separator + "Server.log")));
    }
    public boolean checkIfAppiumServerIsRunning(int port) throws Exception {
        boolean isAppiumServerRunning = false;
        ServerSocket socket;
        try {
            socket = new ServerSocket(port);
            socket.close();
        } catch (IOException e) {
            System.out.println("1");
            isAppiumServerRunning = true;
        } finally {
            socket = null;
        }
        return isAppiumServerRunning;
    }
}

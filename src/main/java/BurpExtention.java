import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.HttpHandler;

public class BurpExtention implements BurpExtension {

    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("NoSQL_Burp_Extension");
        api.logging().logToOutput("NoSQL_Burp_Extension has been loaded");

        HttpHandler httpHandler = new ExtensionHttpHandler();
        api.http().registerHttpHandler(httpHandler);
    }
}

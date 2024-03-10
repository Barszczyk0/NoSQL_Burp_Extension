import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class BurpExtention implements BurpExtension {

    @Override
    public void initialize(MontoyaApi montoyaApi) {
        montoyaApi.extension().setName("NoSQL_Burp_Extension");
        montoyaApi.logging().logToOutput("Hello World!");
    }
}

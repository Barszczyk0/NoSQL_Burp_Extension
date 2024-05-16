import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.HttpHandler;
import burp.api.montoya.ui.menu.BasicMenuItem;
import burp.api.montoya.ui.menu.Menu;
import burp.api.montoya.ui.menu.MenuItem;

import java.util.ArrayList;

public class BurpExtention implements BurpExtension {
    private ArrayList<Payload> payloadsArrayList = new ArrayList<>();

    private void LoadPayloads() {
        this.payloadsArrayList.add(new Payload(PayloadType.FUZZ_STRING, "'\"`{;$Foo}$Foo \\xYZ", "'\"`{;$Foo}$Foo \\xYZ", null));

    }

    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("NNN");
        api.logging().logToOutput("Extension NoSQL Needle Ninja (NNN) has been started");
        this.LoadPayloads();

        api.userInterface().registerContextMenuItemsProvider(new MyContextMenuItemsProvider(api));

//        HttpHandler httpHandler = new MyHttpHandler();
//        api.http().registerHttpHandler(httpHandler);
    }

}

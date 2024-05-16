import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyContextMenuItemsProvider implements ContextMenuItemsProvider
{

    private final MontoyaApi api;

    public MyContextMenuItemsProvider(MontoyaApi api)
    {
        this.api = api;
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event)
    {
        if (event.isFromTool(ToolType.PROXY, ToolType.TARGET, ToolType.LOGGER))
        {
            List<Component> menuItemList = new ArrayList<>();

            JMenuItem retrieveRequestItem = new JMenuItem("Use payload type: AUTHENTICATION_BYPASS");
            JMenuItem retrieveResponseItem = new JMenuItem("Use payload type: BOOLEAN");

            HttpRequestResponse requestResponse = event.messageEditorRequestResponse().isPresent() ? event.messageEditorRequestResponse().get().requestResponse() : event.selectedRequestResponses().get(0);

            retrieveRequestItem.addActionListener(l -> api.logging().logToOutput("Request is:\r\n" + requestResponse.request().toString()));
            menuItemList.add(retrieveRequestItem);

            if (requestResponse.response() != null)
            {
                retrieveResponseItem.addActionListener(l -> api.logging().logToOutput("Response is:\r\n" + requestResponse.response().toString()));
                menuItemList.add(retrieveResponseItem);
            }

            return menuItemList;
        }

        return null;
    }
}
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.handler.HttpRequestToBeSent;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MyContextMenuItemsProvider implements ContextMenuItemsProvider
{

    private final MontoyaApi api;
    private MyHttpHandler myHttpHandler;

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
            event.messageEditorRequestResponse().get().selectionContext();

            JMenuItem selection_FUZZ_STRING = new JMenuItem("Use payload type: FUZZ_STRING");
            JMenuItem selection_BOOLEAN = new JMenuItem("Use payload type: BOOLEAN");
            JMenuItem selection_AUTHENTICATION_BYPASS = new JMenuItem("Use payload type: AUTHENTICATION_BYPASS");
            JMenuItem selection_DATA_EXTRACTION = new JMenuItem("Use payload type: DATA_EXTRACTION");
            JMenuItem selection_TIME_BASED = new JMenuItem("Use payload type: TIME_BASED");

            String startIndexSelected = event.messageEditorRequestResponse().isPresent() ?
                    String.valueOf(event.messageEditorRequestResponse().get().selectionOffsets().get().startIndexInclusive()) :
                    "";

            String endIndexSelected = event.messageEditorRequestResponse().isPresent() ?
                    String.valueOf(event.messageEditorRequestResponse().get().selectionOffsets().get().endIndexExclusive()) :
                    "";

            HttpRequestResponse requestResponse = event.messageEditorRequestResponse().isPresent() ? event.messageEditorRequestResponse().get().requestResponse() : event.selectedRequestResponses().get(0);
            String selectedText = requestResponse.request().toString().substring(Integer.parseInt(startIndexSelected), Integer.parseInt(endIndexSelected));

            //            selection_FUZZ_STRING.addActionListener(l -> api.logging().logToOutput("Request is:\r\n" + requestResponse.request().toString()));


            selection_FUZZ_STRING.addActionListener(l -> NNN.test(requestResponse, Integer.parseInt(startIndexSelected), Integer.parseInt(endIndexSelected)));
            selection_BOOLEAN.addActionListener(l -> api.logging().logToOutput("Selected text is (from boolean):\r\n" + startIndexSelected + endIndexSelected));
            selection_AUTHENTICATION_BYPASS.addActionListener(l -> api.logging().logToOutput("Selected text is (from authentication bypass):\r\n" + startIndexSelected + endIndexSelected));
            selection_DATA_EXTRACTION.addActionListener(l -> api.logging().logToOutput("Selected text is (from data extraction):\r\n" + startIndexSelected + endIndexSelected));
            selection_TIME_BASED.addActionListener(l -> api.logging().logToOutput("Selected text is (from time based):\r\n" + startIndexSelected + endIndexSelected));

            menuItemList.add(selection_FUZZ_STRING);
            menuItemList.add(selection_BOOLEAN);
            menuItemList.add(selection_AUTHENTICATION_BYPASS);
            menuItemList.add(selection_DATA_EXTRACTION);
            menuItemList.add(selection_TIME_BASED);

            return menuItemList;
        }

        return null;
    }


}
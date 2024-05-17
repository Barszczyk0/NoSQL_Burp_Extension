import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.HttpResponseReceived;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.HttpResponseEditor;

import javax.swing.*;
import java.awt.*;

import java.util.ArrayList;

import static burp.api.montoya.ui.editor.EditorOptions.READ_ONLY;

public class NNN implements BurpExtension {
    private MontoyaApi api;
    private ArrayList<Payload> payloadsArrayList = new ArrayList<>();

    private void LoadPayloads() {
        this.payloadsArrayList.add(new Payload(PayloadType.FUZZ_STRING, "'\"`{;$Foo}$Foo \\xYZ", "'\"`{;$Foo}$Foo \\xYZ", null));

    }

    @Override
    public void initialize(MontoyaApi api) {
        this.api = api;
        api.extension().setName("NNN");
        api.logging().logToOutput("Extension NoSQL Needle Ninja (NNN) has been started");
        this.LoadPayloads();

        api.userInterface().registerContextMenuItemsProvider(new MyContextMenuItemsProvider(api));

        MyTableModel tableModel = new MyTableModel();
        api.userInterface().registerSuiteTab("NNN Logger", constructLoggerTab(tableModel));
        api.http().registerHttpHandler(new MyHttpHandler(tableModel));

//        HttpHandler httpHandler = new MyHttpHandler();
//        api.http().registerHttpHandler(httpHandler);
    }

    private Component constructLoggerTab(MyTableModel tableModel)
    {
        // main split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // tabs with request/response viewers
        JTabbedPane tabs = new JTabbedPane();

        UserInterface userInterface = api.userInterface();

        HttpRequestEditor requestViewer = userInterface.createHttpRequestEditor(READ_ONLY);
        HttpResponseEditor responseViewer = userInterface.createHttpResponseEditor(READ_ONLY);

        tabs.addTab("Request", requestViewer.uiComponent());
        tabs.addTab("Response", responseViewer.uiComponent());

        splitPane.setRightComponent(tabs);

        // table of log entries
        JTable table = new JTable(tableModel)
        {
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend)
            {
                // show the log entry for the selected row
                HttpResponseReceived responseReceived = tableModel.get(rowIndex);
                requestViewer.setRequest(responseReceived.initiatingRequest());
                responseViewer.setResponse(responseReceived);

                super.changeSelection(rowIndex, columnIndex, toggle, extend);
            }
        };

        // Columns sizes
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setMaxWidth(90);
        table.getColumnModel().getColumn(4).setMaxWidth(90);
        table.getColumnModel().getColumn(5).setMaxWidth(90);

        table.setAutoCreateRowSorter(true);


        JScrollPane scrollPane = new JScrollPane(table);

        splitPane.setLeftComponent(scrollPane);

        return splitPane;
    }


}

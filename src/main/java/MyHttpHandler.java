import burp.api.montoya.http.handler.*;

public class MyHttpHandler implements HttpHandler
{
    private final MyTableModel tableModel;

    public MyHttpHandler(MyTableModel tableModel)
    {
        this.tableModel = tableModel;
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent)
    {
        return RequestToBeSentAction.continueWith(requestToBeSent);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived)
    {
        tableModel.add(responseReceived);
        return ResponseReceivedAction.continueWith(responseReceived);
    }
}
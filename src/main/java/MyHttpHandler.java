import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;

public class MyHttpHandler implements HttpHandler {
    public MyHttpHandler() {}
    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {
//        if (httpRequestToBeSent.isInScope()) {
//        }
//        return null;
        // Modify request
        HttpRequest request = httpRequestToBeSent.withAddedHeader("Test", "TestValue");
        return RequestToBeSentAction.continueWith(request);



    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {
        return null;
    }
}

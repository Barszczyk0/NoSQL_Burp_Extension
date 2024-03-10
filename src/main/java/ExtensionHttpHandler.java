import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;

public class ExtensionHttpHandler implements HttpHandler {
    public ExtensionHttpHandler() {}
    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {
        if (httpRequestToBeSent.isInScope()) {
            HttpRequest request = httpRequestToBeSent;
            // Modify request
            return RequestToBeSentAction.continueWith(request);
        }
        return null;
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {
        return null;
    }
}

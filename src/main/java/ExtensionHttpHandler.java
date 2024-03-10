import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;

public class ExtensionHttpHandler implements HttpHandler {
    public ExtensionHttpHandler() {}
    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {
        if (httpRequestToBeSent.isInScope()) {
            // Modify request
            return RequestToBeSentAction.continueWith(httpRequestToBeSent);
        }
        return null;
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {
        return null;
    }
}

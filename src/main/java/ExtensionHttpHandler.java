import burp.api.montoya.http.handler.*;
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

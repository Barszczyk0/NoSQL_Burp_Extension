import java.util.ArrayList;

public class Payload {
    public String payload_GET;
    public String payload_POST;
    public PayloadType payloadType;
    public ArrayList<String> errorMessage;

    public Payload(PayloadType payloadType, String payload_GET, String payload_POST, ArrayList<String> errorMessage) {
        this.payloadType = payloadType;
        this.payload_GET = payload_GET;
        this.payload_POST = payload_POST;
        this.errorMessage = errorMessage;
    }

    public String getPayload_GET() {
        return payload_GET;
    }

    public void setPayload_GET(String payload_GET) {
        this.payload_GET = payload_GET;
    }

    public String getPayload_POST() {
        return payload_POST;
    }

    public void setPayload_POST(String payload_POST) {
        this.payload_POST = payload_POST;
    }

    public PayloadType getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(PayloadType payloadType) {
        this.payloadType = payloadType;
    }

    public ArrayList<String> getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ArrayList<String> errorMessage) {
        this.errorMessage = errorMessage;
    }
}

import java.util.ArrayList;

public class Payload {
    public String payloadUrlEncoded;
    public String payload;
    public PayloadType payloadType;
    public ArrayList<String> errorMessage;

    public Payload(PayloadType payloadType, String payloadUrlEncoded, String payload, ArrayList<String> errorMessage) {
        this.payloadType = payloadType;
        this.payloadUrlEncoded = payloadUrlEncoded;
        this.payload = payload;
        this.errorMessage = errorMessage;
    }

    public String getPayloadUrlEncoded() {
        return payloadUrlEncoded;
    }

    public void setPayloadUrlEncoded(String payloadUrlEncoded) {
        this.payloadUrlEncoded = payloadUrlEncoded;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
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

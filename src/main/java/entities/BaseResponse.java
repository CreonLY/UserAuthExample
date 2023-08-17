package entities;

public class BaseResponse {
    String message;
    int statusCode;
    Object data;

    public BaseResponse(String message) {
        this(message, "");
    }

    public BaseResponse(String message, Object data) {
        this(message, 200, data);
    }

    public BaseResponse(String message, int statusCode, Object data) {
        this.message = message;
        this.statusCode = statusCode;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

package server;

public enum ResponseCodes {
    OK(200),
    NOT_FOUND(404),
    ALREADY_EXISTS(409);

    private final int code;

    ResponseCodes(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

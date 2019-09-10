package edu.uta.cse.microservice.user.response;

import java.io.Serializable;

/**
 JSON Response
 */

public class Response implements Serializable {

    public static final Response USERNAME_PASSWORD_INVALID = new Response("1001", "username or password is invalid");
    public static final Response MOBILE_OR_EMAIL_REQUIRED = new Response("1002", "mobile number or email is required");
    public static final Response SEND_VERIFY_CODE_FAILED = new Response("1003", "send verify code failed");
    public static final Response VERIFY_CODE_INVALID = new Response("1003", "verify code invalid");
    public static final Response SUCCESS = new Response();

    private String code;
    private String message;

    public Response() {
        this.code = "0";
        this.message = " success";
    }

    public Response(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /* Exception for demo purpose, a better way to handle server exceptions is to use a Circuit Breaker */
    public static Response exception(Exception e) {
        e.printStackTrace();
        return new Response("9999", e.getMessage());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

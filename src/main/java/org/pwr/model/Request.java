package org.pwr.model;

/**
 * Created by mkonczyk on 2016-10-25.
 */
public class Request {
    private String request;

    public Request(String request) {
        this.request = request;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}

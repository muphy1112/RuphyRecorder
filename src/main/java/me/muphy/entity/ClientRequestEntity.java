package me.muphy.entity;

import java.io.Serializable;
import java.util.Date;

public class ClientRequestEntity implements Serializable {
    private String id;
    private String url;
    private String cookie;
    private String serverIpAddr;
    private String clientIpAddr;
    private String parameters;
    private String queryString;
    private String user;
    private String method;
    private String referrer;
    private Date visitTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getServerIpAddr() {
        return serverIpAddr;
    }

    public void setServerIpAddr(String serverIpAddr) {
        this.serverIpAddr = serverIpAddr;
    }

    public String getClientIpAddr() {
        return clientIpAddr;
    }

    public void setClientIpAddr(String clientIpAddr) {
        this.clientIpAddr = clientIpAddr;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public Date getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }

    @Override
    public String toString() {
        return "ClientRequestEntity{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", cookie='" + cookie + '\'' +
                ", serverIpAddr='" + serverIpAddr + '\'' +
                ", clientIpAddr='" + clientIpAddr + '\'' +
                ", parameters='" + parameters + '\'' +
                ", queryString='" + queryString + '\'' +
                ", user='" + user + '\'' +
                ", method='" + method + '\'' +
                ", referrer='" + referrer + '\'' +
                ", visitTime=" + visitTime +
                '}';
    }
}

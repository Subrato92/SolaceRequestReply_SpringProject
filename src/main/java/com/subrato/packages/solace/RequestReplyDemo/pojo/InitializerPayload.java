package com.subrato.packages.solace.RequestReplyDemo.pojo;


public class InitializerPayload {
    private String userName;
    private String password;
    private String host;
    private String vpn;

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public String getVpn() {
        return vpn;
    }
    public void setVpn(String vpn) {
        this.vpn = vpn;
    }

}

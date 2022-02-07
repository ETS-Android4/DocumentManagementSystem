package org.unibl.etf.tokengenerator.retrofit;

public class Token {
    private String token;

    public Token(String value) {
        this.token = value;
    }

    public String getValue() {
        return token;
    }

    public void setValue(String value) {
        this.token = value;
    }

}

package org.pwr.model;

import java.math.BigInteger;

/**
 * Created by mkonczyk on 2016-10-25.
 */
public class RequestValues {
    BigInteger a, b, p, g;

    public RequestValues() {
    }

    public RequestValues(BigInteger a) {
        this.a = a;
    }

    public RequestValues(BigInteger p, BigInteger g) {
        this.p = p;
        this.g = g;
    }

    public BigInteger getA() {
        return a;
    }

    public void setA(BigInteger q) {
        this.a = a;
    }

    public BigInteger getB() {
        return b;
    }

    public void setB(BigInteger b) {
        this.b = b;
    }

    public BigInteger getP() {
        return p;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public BigInteger getG() {
        return g;
    }

    public void setG(BigInteger g) {
        this.g = g;
    }


}

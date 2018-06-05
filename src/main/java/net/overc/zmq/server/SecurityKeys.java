package net.overc.zmq.server;

import zmq.io.mechanism.curve.Curve;

/**
 * Date: 5/30/18
 *
 * @author Vitalii Siryi
 */
public class SecurityKeys {

    private static SecurityKeys keys = new SecurityKeys();

    private String publicKey = "lZY<NZ^IBrLBvl0+[jHO.caUvp?qP6n%.mdOPNsX";

    private String secretKey = "<Yj-AqI*bD.KarpHs9PHgxwpHK+x/N?WMm}9e{{F";

    /*public SecurityKeys() {
        prepareCurveKeys();

        System.out.println("publicKey["+publicKey+"]");
        System.out.println("secretKey["+secretKey+"]");
    }*/

    public static SecurityKeys get(){
        return keys;
    }

    private void prepareCurveKeys() {
        Curve curve = new Curve();
        String[] serverKeys = curve.keypairZ85();
        this.publicKey = serverKeys[0];
        this.secretKey = serverKeys[1];
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getSecretKey() {
        return secretKey;
    }
}

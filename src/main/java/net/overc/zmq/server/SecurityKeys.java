package net.overc.zmq.server;

/**
 * Date: 5/30/18
 *
 * @author Vitalii Siryi
 */
public class SecurityKeys {

    private static SecurityKeys keys = new SecurityKeys();

    private String publicKey = "lZY<NZ^IBrLBvl0+[jHO.caUvp?qP6n%.mdOPNsX";

    private String secretKey = "<Yj-AqI*bD.KarpHs9PHgxwpHK+x/N?WMm}9e{{F";

    public static SecurityKeys get(){
        return keys;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getSecretKey() {
        return secretKey;
    }
}

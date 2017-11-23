package wish;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by jeppe on 12/15/16.
 */

public class LocalDiscovery implements Serializable{

    private String alias;
    private String type;
    private byte[] luid;
    private byte[] ruid;
    private byte[] rhid;
    private byte[] pubkey;
    private boolean claim;


    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getLuid() {
        return luid;
    }

    public void setLuid(byte[] luid) {
        this.luid = luid;
    }

    public byte[] getRuid() {
        return ruid;
    }

    public void setRuid(byte[] ruid) {
        this.ruid = ruid;
    }

    public byte[] getRhid() {
        return rhid;
    }

    public void setRhid(byte[] rhid) {
        this.rhid = rhid;
    }


    public byte[] getPubkey() {
        return pubkey;
    }

    public void setPubkey(byte[] pubkey) {
        this.pubkey = pubkey;
    }

    public boolean isClaim() {
        return claim;
    }

    public void setClaim(boolean claim) {
        this.claim = claim;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null) {
            return false;
        }

        if (getClass() != object.getClass()) {
            return false;
        }

        LocalDiscovery localDiscovery = (LocalDiscovery) object;


        return Arrays.equals(luid, localDiscovery.getLuid())
                && Arrays.equals(ruid, localDiscovery.getRuid())
                && Arrays.equals(rhid, localDiscovery.getRhid())
                && Arrays.equals(pubkey, localDiscovery.getPubkey())
                && alias.equals(localDiscovery.getAlias())
                && type.equals(localDiscovery.getType());
    }

}


package wish;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by jeppe on 12/28/16.
 */

public class Connection implements Serializable{

    private int cid;
    private byte[] luid;
    private byte[] ruid;
    private byte[] rhid;
    private boolean outgoing;
    private boolean relay;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
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

    public boolean isOutgoing() {
        return outgoing;
    }

    public boolean isRelayed() {
        return relay;
    }

    public void setOutgoing(boolean outgoing) {
        this.outgoing = outgoing;
    }

    public void setRelay(boolean relay) {
        this.relay = relay;
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

        Connection connection = (Connection) object;

        return Arrays.equals(luid, connection.getLuid())
                && Arrays.equals(ruid, connection.getRuid())
                && Arrays.equals(rhid, connection.getRhid())
                && cid == connection.getCid()
                && outgoing == connection.isOutgoing();
    }
}

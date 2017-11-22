package wish.request;

import java.util.List;

import wish.LocalDiscovery;

/**
 * Created by jeppe on 11/30/16.
 */

public class Wld {

    /**
     *
     * @param callback Wld.ListCb
     * @return
     */
    public static int list(ListCb callback) {
       return WldList.request(callback);
    }

    /**
     *
     * @param callback Wld.ClearCb
     * @return
     */
    public static int clear(ClearCb callback) {
       return WldClear.request(callback);
    }

    /**
     *
     * @param callback Wld.AnnounceCb
     * @return
     */
    public static int announce(AnnounceCb callback) {
     return WldAnnounce.request(callback);
    }

    /**
     *
     * @param luid Byte array of identity luid
     * @param ruid Byte array of identity ruid
     * @param rhid Byte array of identity rhid
     * @param callback Wld.FriendRequest
     * @return
     */
    public static int friendRequest(byte[] luid, byte[] ruid, byte[] rhid, Wld.FriendRequestCb callback) {
       return WldFriendRequest.request(luid, ruid, rhid, callback);
    }

    public abstract static class ListCb extends Callback {
        public abstract void cb(List<LocalDiscovery> localDiscoveries);
    }


    public abstract static class ClearCb extends Callback {
        public abstract void cb(boolean value);
    }


    public abstract static class AnnounceCb extends Callback {
        public abstract void cb(boolean value);
    }

    public abstract static class FriendRequestCb extends Callback {
        public abstract void cb(boolean value);
    }



}

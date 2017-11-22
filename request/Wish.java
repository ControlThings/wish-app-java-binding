package wish.request;

import wish.Connection;
import wish.WishApp;

/**
 * Created by jeppe on 11/30/16.
 */

public class Wish {

    /**
     *
     * @param callback Wish.VersionCb
     * @return
     */
    public static int version(VersionCb callback) {
       return WishVersion.request(callback);
    }

    /**
     *
     * @param callback Wish.SignalsCb
     * @return
     */
    public static int  signals(SignalsCb callback) {
        return WishSignals.request(null, callback);
    }

    /**
     *
     * @param connection wish connection
     * @param callback Wish.SignalsCb
     * @return
     */
    public static int signals(Connection connection, SignalsCb callback) {
        return WishSignals.request(connection, callback);
    }

    /**
     *
     * @param id Int of wish request
     */
    public static void cancel(int id) {
        WishApp.getInstance().requestCancel(id);
    }

    public abstract static class VersionCb extends Callback {
        public abstract void cb(String version);
    }

    public abstract static class SignalsCb extends Callback {
        public abstract void cb(String signal);
    }


}


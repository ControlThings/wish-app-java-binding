package wish;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import addon.AddonException;
import addon.WishFile;


/**
 * Created by jan on 11/1/16.
 */

public class WishApp {
    /** startWishApp return value for success */
    private static final int WISH_APP_SUCCESS = 0;
    /** startWishApp return error return if started multiple times */
    private static final int WISH_APP_ERROR_MULTIPLE_TIMES = -1;
    /** startWishApp return error return for other errors */
    private static final int WISH_APP_ERROR_UNSPECIFIED = -10;

    private static List<Error> wishErrorHandleList = new ArrayList<>();;

    static {
        System.loadLibrary("mist");
    }

    /* Private constructor must exist to enforce Singleton pattern */
    private WishApp() {}


    private static class MistNodeHolder {
        private static final WishApp INSTANCE = new WishApp();
    }

    public static WishApp getInstance() {
        return MistNodeHolder.INSTANCE;
    }

    public void startWishApp(Context context) {
        String appName = context.getPackageName();
        if (appName.length() > 32) {
            appName = appName.substring(0, 32);
        }

        int ret = startWishApp(appName, new WishFile(context));

        if (ret != WISH_APP_SUCCESS) {
            if (ret == WISH_APP_ERROR_MULTIPLE_TIMES) {
                throw new AddonException("Addon cannot be started multiple times.");
            }
            else {
                throw new AddonException("Unspecified Addon exception");
            }
        }
    }

    synchronized native int startWishApp(String appName, WishFile wishFile);
    synchronized native void stopWishApp();

    /**
     * Send a Wish request to the local Wish core
     * @param req the request in BSON format
     * @return the RPC id; The invalid RPC id 0 is returned for any errors.
     */
    public synchronized native int request(byte[] req, RequestCb cb); //will call  wish_app_core_with_cb_context

    public synchronized native void requestCancel(int id);

    /** This method simply pretty-prints a BSON object to the system console
     * @param tag the console log tag
     * @param bson_bytes the BSON object to be pretty printed
     */
    public static native void bsonConsolePrettyPrinter(String tag, byte[] bson_bytes);


    void online(byte[] peerBson) {

    }

    void offline(byte[] peerBson) {

    }

    static void registerWishRpcErrorHandler(Error error) {
        synchronized (wishErrorHandleList) {
            wishErrorHandleList.add(error);
        }
    }

    interface Error {
        public void cb(int code, String msg);
    }

    public abstract static class RequestCb {

        /**
         * The callback invoked when "ack" is received for a RPC request
         *
         * @param data a document containing RPC return value as 'data' element
         */
        public void ack(byte[] data) {
            response(data);
            end();
        };

        /**
         * The callback invoked when "sig" is received for a RPC request
         *
         * @param data the contents of 'data' element of the RPC reply
         */
        public void sig(byte[] data) {
            response(data);
        };

        public abstract void response(byte[] data);
        public abstract void end();

        /**
         * The callback invoked when "err" is received for a failed RPC request
         *
         * @param code the error code
         * @param msg  a free-text error message
         */
        public void err(int code, String msg) {
            synchronized (wishErrorHandleList) {
                for (Error error : wishErrorHandleList) {
                    error.cb(code, msg);
                }
            }
        };
    }
}

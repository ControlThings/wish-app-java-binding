package wishApp;

import android.content.Context;

import addon.WishFile;


/**
 * Created by jan on 11/1/16.
 */

public class WishApp {
    private Context context;
    private WishFile file;

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
        this.context = context;
        file = new WishFile(context);

        String appName = context.getPackageName();
        if (appName.length() > 32) {
            appName = appName.substring(0, 32);
        }
        startWishApp(appName);
    }

    synchronized native void startWishApp(String appName);
    synchronized native void stopWishApp();

    /**
     * Send a Wish request to the local Wish core
     * @param req the request in BSON format
     * @return the RPC id; The invalid RPC id 0 is returned for any errors.
     */
    public synchronized native int request(byte[] req, RequestCb cb); //will call  wish_app_core_with_cb_context

    public synchronized native int requestCancel(int id);

    /** This method simply pretty-prints a BSON object to the system console
     * @param tag the console log tag
     * @param bson_bytes the BSON object to be pretty printed
     */
    public static native void bsonConsolePrettyPrinter(String tag, byte[] bson_bytes);


    void online(byte[] peerBson) {

    }

    void offline(byte[] peerBson) {

    }

    /**
     * Open a file
     *
     * @param filename the filename
     * @return the file ID number which can be used with read, write..., or -1 for an error
     */
    public int openFile(String filename) {
        return file.open(filename);
    }

    /**
     * Close a fileId
     *
     * @param fileId the fileId to close
     * @return 0 for success, -1 for an error
     */
    public int closeFile(int fileId) {
        return file.close(fileId);
    }

    /**
     * Read from a file
     *
     * @param fileId the fileId, obtained with open()
     * @param buffer The buffer to place the bytes into
     * @param count  the number of bytes to read
     * @return the amount of bytes read, or 0 if EOF, or -1 for an error
     */
    public int readFile(int fileId, byte[] buffer, int count) {
        if (count != buffer.length) {
            return -1;
        }
        return file.read(fileId, buffer, count);
    }

    /**
     * Write to a file in internal storage
     *
     * @param fileId the fileId
     * @param buffer the databuffer to be written
     * @param count  The
     * @return
     */
    public int writeFile(int fileId, byte[] buffer, int count) {
        if (count != buffer.length) {
            return -1;
        } else {
            return file.write(fileId, buffer);
        }
    }

    public int seekFile(int fileId, int offset, int whence) {
        return (int) file.seek(fileId, offset, whence);
    }

    public int rename(String oldName, String newName) {
        return file.rename(oldName, newName);
    }

    public int remove(String filename) {
        return file.remove(filename);
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
        public abstract void err(int code, String msg);
    }
}

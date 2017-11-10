package wishApp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import mistNode.MistNode;


/**
 * Created by jeppe on 3/2/17.
 */
public class Errors {

    private static Errors instance;

    static Errors getInstance() {
        if (instance == null) {
            instance = new Errors();
        }
        return instance;
    }


    private int id;
    private HashMap<Integer, ListenCb> wishCbHashMap;

    private Errors() {
        id = 1;
        wishCbHashMap = new HashMap<>();
        WishApp.registerWishRpcErrorHandler(new WishApp.Error() {
            @Override
            public void cb(int code, String msg) {
                wishError(code, msg);
            }
        });

    }

    private int registerCb(ListenCb cb) {
        wishCbHashMap.put(id, cb);
        return id++;
    }

    public static int listen(ListenCb callback) {
        return getInstance().registerCb(callback);
    }

    public interface ListenCb {
        public void cb(int code, String msg);
    }

    public static void cancel(int id) {
        if (getInstance().wishCbHashMap.containsKey(id)) {
            getInstance().wishCbHashMap.remove(id);
            return;
        }
    }

    static void wishError(int code, String msg) {
        Iterator iterator = getInstance().wishCbHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            ListenCb listenCb = (ListenCb) pair.getValue();
            listenCb.cb(code, msg);
            iterator.remove();
        }
    }

}

package wish.request;

import java.util.ArrayList;


/**
 * Created by jeppe on 11/30/16.
 */

public class Connection {

    public static void disconect(int cid, DisconectCb callback) {
        ConnectionDisconnect.request(cid, callback);
    }

    public static void list(ListCb callback) {
        ConnectionList.request(callback);
    }

    public interface DisconectCb extends Callback {
        public void cb(boolean state);
    }


    public interface ListCb extends Callback {
        public void cb(ArrayList<wish.Connection> connections);
    }
}

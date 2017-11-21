package wish.request;

import org.bson.BsonArray;

import java.util.List;


/**
 * Created by jeppe on 11/14/17.
 */

public class Connection {

    /**
     *
     * @param connection wish connection
     * @param op String op
     * @param array Bson array
     * @param callback Connection.RequestCb
     * @return
     */
    public static int request(wishApp.Connection connection, String op, BsonArray array, Connection.RequestCb callback) {
        return ConnectionRequest.request(connection, op, array, callback);
    }

    /**
     *
     * @param callback Connection.ListCb
     * @return
     */
    public static int list(ListCb callback) {
       return ConnectionList.request(callback);
    }

    /**
     *
     * @param cid Connection cid
     * @param callback Connection.DisconectCb
     * @return
     */
    public static int disconect(int cid, DisconectCb callback) {
       return ConnectionDisconnect.request(cid, callback);
    }

    public abstract static class ListCb extends Callback {
        public abstract void cb(List<wishApp.Connection> connections);
    }

    public abstract static class DisconectCb extends Callback {
        public abstract void cb(boolean value);
    }

<<<<<<< HEAD
    public abstract static class RequestCb extends Callback {
        public abstract void cb (byte[] bson);
=======
    public interface ListCb extends Callback {
        public void cb(ArrayList<wish.Connection> connections);
>>>>>>> 6fcd683c362d9bebffbebfdf4fcd9fa28425ffd8
    }
}

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
    public static int request(wish.Connection connection, String op, BsonArray array, Connection.RequestCb callback) {
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

    public static int checkConnections(CheckConnectionsCb callback) {
        return ConnectionCheckConnections.request(callback);
    }

    public abstract static class ListCb extends Callback {
        public abstract void cb(List<wish.Connection> connections);
    }

    public abstract static class DisconectCb extends Callback {
        public abstract void cb(boolean value);
    }

    public abstract static class RequestCb extends Callback {
        public abstract void cb(byte[] bson);
    }

    public abstract static class CheckConnectionsCb extends Callback {
        public abstract void cb(boolean response);
    }

}

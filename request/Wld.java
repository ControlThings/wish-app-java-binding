package wishApp.request;

import org.bson.BsonDocument;


import java.util.ArrayList;

import wishApp.LocalDiscovery;

/**
 * Created by jeppe on 11/30/16.
 */

public class Wld {

    public static void acceptFriendRequest(String id, AcceptFriendRequestCb callback) {
        WldAcceptFriendRequest.request(id,callback);
    }

    public static void clear(ClearCb callback) {
        WldClear.request(callback);
    }

    public static void connect(ConnectCb callback) {
        WldConnect.request(callback);
    }

    public static void friendRequest(byte[] luid, byte[] ruid, byte[] rhid, Wld.FriendRequestCb callback) {
        WldFriendRequest.request(luid, ruid, rhid, callback);
    }

    public static void list(ListCb callback) {
        WldList.request(callback);
    }

    public static void listFriendRequests(ListFriendRequestsCb callback) {
        WldListFriendRequests.request(callback);
    }

    public static void listFriendRequestsPending(ListFriendRequestsPendingCb callback) {
        WldListFriendRequestsPending.request(callback);
    }

    public interface AcceptFriendRequestCb extends CallbackInterface {
        public void cb();
    }

    public interface ClearCb extends CallbackInterface {
        public void cb();
    }

    public interface ConnectCb extends CallbackInterface {
        public void cb();
    }

    public interface FriendRequestCb extends CallbackInterface {
        public void cb();
    }

    public interface ListCb extends CallbackInterface {
        public void cb(ArrayList<LocalDiscovery> connections);
    }

    public interface ListFriendRequestsCb extends CallbackInterface {
        public void cb(BsonDocument data);
    }

    public interface ListFriendRequestsPendingCb extends CallbackInterface {
        public void cb(BsonDocument data);
    }
}

<<<<<<< HEAD
package wishApp.request;
;
=======
package wish.request;

>>>>>>> 6fcd683c362d9bebffbebfdf4fcd9fa28425ffd8
import org.bson.BsonDocument;

import java.util.List;

<<<<<<< HEAD
import wishApp.Cert;
import wishApp.Connection;
import wishApp.Peer;
import wishApp.Request;
=======
import wish.Cert;
import wish.Connection;
import wish.Peer;
import wish.Friend;
import wish.MistIdentity;
>>>>>>> 6fcd683c362d9bebffbebfdf4fcd9fa28425ffd8

/**
 * Created by jeppe on 10/24/17.
 */

public class Identity {

    /**
     *
     * @param alias String alias
     * @param callback Identity.CreateCb
     * @return
     */
    public static int create(String alias, CreateCb callback) {
        return IdentityCreate.request(alias, callback);
    }

    /**
     *
     * @param uid Byte array of identity uid
     * @param callback Identity.ExportCb
     * @return
     */
    public static int export(byte[] uid, ExportCb callback) {
        return IdentityExport.request(null, uid, callback);
    }

    /**
     *
     * @param connection wish connection
     * @param id Byte array of identity uid
     * @param callback Identity.ExportCb
     */
    public static int export(Connection connection, byte[] id, ExportCb callback) {
        return IdentityExport.request(connection, id, callback);
    }

    /**
     *
     * @param identity a BSON representation of wish identity {alias, uid, pubkey}
     * @param callback Identity.ImportCb
     */
    public static int _import(byte[] identity, ImportCb callback) {
       return IdentityImport.request(identity, callback);
    }

    /**
     *
     * @param callback Identity.ListCb
     * @return
     */
    public static int list(ListCb callback) {
        return IdentityList.request(null, callback);
    }

    /**
     *
     * @param connection wish connection
     * @param callback Identity.ListCb
     * @return
     */
    public static int list(Connection connection, ListCb callback) {
        return IdentityList.request(connection, callback);
    }

    /**
     *
     * @param uid Byte array of identity uid
     * @param callback Identity.GetCb
     * @return
     */
    public static int get(byte[] uid, GetCb callback) {
        return IdentityGet.request(uid, callback);
    };

    //todo change to wishApp
    public static void friendRequest(byte[] luid, BsonDocument contact, FriendRequestCb callback) {
        IdentityFriendRequest.request(luid, contact, null, callback);
    }

    //todo change to wishApp
    public static void friendRequest(byte[] luid, BsonDocument contact, Peer peer, FriendRequestCb callback) {
        IdentityFriendRequest.requestDocument(luid, contact, peer, callback);
    }

    /**
     *
     * @param callback Identity.FriendRequestListCb
     */
    public static int friendRequestList(FriendRequestListCb callback) {
       return IdentityFriendRequestList.request(null, callback);
    }

    /**
     *
     * @param connection wish connection
     * @param callback Identity.FriendRequestListCb
     */
    public static int friendRequestList(Connection connection, FriendRequestListCb callback) {
        return IdentityFriendRequestList.request(connection, callback);
    }

    /**
     *
     * @param luid Byte array of local uid
     * @param ruid Byte array of remote uid
     * @param callback Identity.FriendRequestAcceptCb
     * @return
     */
    public static int friendRequestAccept(byte[] luid, byte[] ruid, FriendRequestAcceptCb callback) {
        return IdentityFriendRequestAccept.request(null, luid, ruid, callback);
    }

    /**
     *
     * @param connection wish connection
     * @param luid Byte array of local uid
     * @param ruid Byte array of remote uid
     * @param callback Identity.FriendRequestAcceptCb
     * @return
     */
    public static int friendRequestAccept(Connection connection, byte[] luid, byte[] ruid, FriendRequestAcceptCb callback) {
        return IdentityFriendRequestAccept.request(connection, luid, ruid, callback);
    }

    /**
     *
     * @param luid Byte array of local uid
     * @param ruid Byte array of remote uid
     * @param callback Identity.FriendRequestAcceptCb
     * @return
     */
    public static int friendRequestDecline(byte[] luid, byte[] ruid, FriendRequestDeclineCb callback) {
        return IdentityFriendRequestDecline.request(null, luid, ruid, callback);
    }

    /**
     *
     * @param connection wish connection
     * @param luid Byte array of local uid
     * @param ruid Byte array of remote uid
     * @param callback Identity.FriendRequestAcceptCb
     * @return
     */
    public static int friendRequestDecline(Connection connection, byte[] luid, byte[] ruid, FriendRequestDeclineCb callback) {
        return IdentityFriendRequestDecline.request(connection, luid, ruid, callback);
    }

    /**
     *
     * @param uid Byte array of local uid
     * @param cert wish certificate
     * @param callback Identity.SignCb
     * @return
     */
    public static int sign(byte[] uid, BsonDocument cert, SignCb callback) {
        return IdentitySign.request(null, uid, cert, callback);
    }

    /**
     *
     * @param connection wish connection
     * @param uid Byte array of local uid
     * @param cert wish certificate
     * @param callback Identity.SignCb
     * @return
     */
    public static int sign(Connection connection, byte[] uid, BsonDocument cert, SignCb callback) {
        return IdentitySign.request(connection, uid, cert, callback);
    }

    /**
     *
     * @param cert wish certificate
     * @param callback Identity.VerifyCb
     * @return
     */
    public static int verify(Cert cert, VerifyCb callback) {
        return IdentityVerify.request(cert, callback);
    }

    /**
     *
     * @param uid Byte array of identity uid
     * @param callback Identity.RemoveCb
     * @return
     */
    public static int remove(byte[] uid, RemoveCb callback) {
        return IdentityRemove.request(null, uid, callback);
    };

    /**
     *
     * @param connection wish connection
     * @param uid Byte array of identity uid
     * @param callback Identity.RemoveCb
     * @return
     */
    public static int remove(Connection connection, byte[] uid, RemoveCb callback) {
        return IdentityRemove.request(connection, uid, callback);
    };

    public abstract static class CreateCb extends Callback {
        public abstract void cb (wishApp.Identity identity);
    }

    public abstract static class ExportCb extends Callback {
        public abstract void cb(byte[] bsonData, byte[] bsonRaw);
    }

    public abstract static class ImportCb extends Callback {
        public abstract void cb(String name, byte[] uid);
    }

    //todo change to wishApp
    public interface FriendRequestCb extends CallbackInterface {
        public void cb(boolean state);
    }

    public abstract static class ListCb extends Callback {
        public abstract void cb (List<wishApp.Identity> identities);
    }

    public abstract static class GetCb extends Callback {
        public abstract void cb (wishApp.Identity identity);
    }

    public abstract static class FriendRequestListCb extends Callback {
        public abstract void cb(List<Request> requests);
    }

    public abstract static class FriendRequestAcceptCb extends Callback {
        public abstract void cb(boolean value);
    }

    public abstract static class FriendRequestDeclineCb extends Callback {
        public abstract void cb(boolean value);
    }

    public abstract static class SignCb extends Callback {
        public abstract void cb(byte[] bsonData);
    }

    public abstract static class VerifyCb extends Callback {
        public abstract void cb(boolean value);
    }

    public abstract static class RemoveCb extends Callback {
        public abstract void cb (boolean value);
    }
}

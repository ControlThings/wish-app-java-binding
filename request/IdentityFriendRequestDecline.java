package wish.request;

import org.bson.BSONException;
import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

<<<<<<< HEAD
import bson.BsonExtendedBinaryWriter;
import bson.BsonExtendedWriter;
import wishApp.Connection;
import wishApp.WishApp;

import static wishApp.request.Callback.BSON_ERROR_CODE;
import static wishApp.request.Callback.BSON_ERROR_STRING;
=======
import wish.Connection;
import wish.Errors;
import wish.RequestInterface;

import static wish.RequestInterface.bsonError;
import static wish.RequestInterface.bsonException;
>>>>>>> 6fcd683c362d9bebffbebfdf4fcd9fa28425ffd8

class IdentityFriendRequestDecline {
    static int request(Connection connection, byte[] luid, byte[] ruid, Identity.FriendRequestDeclineCb callback) {
        String op = "identity.friendRequestDecline";

        BsonArray array = new BsonArray();
        array.add(new BsonBinary(luid));
        array.add(new BsonBinary(ruid));

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonExtendedWriter writer = new BsonExtendedBinaryWriter(buffer);
        writer.writeStartDocument();

        writer.writeString("op", op);

        writer.writeStartArray("args");
        writer.pipeArray(array);
        writer.writeEndArray();

        writer.writeInt32("id", 0);

        writer.writeEndDocument();
        writer.flush();


        WishApp.RequestCb requestCb = new WishApp.RequestCb() {
            Identity.FriendRequestDeclineCb cb;

            @Override
            public void response(byte[] data) {
                try {
                    BsonDocument bson = new RawBsonDocument(data);
                    boolean value = bson.getBoolean("data").getValue();
                    cb.cb(value);
                } catch (BSONException e) {
                    cb.err(BSON_ERROR_CODE, BSON_ERROR_STRING);
                }
            }

            @Override
            public void end() {
                cb.end();
            }

            @Override
            public void err(int code, String msg) {
                super.err(code, msg);
                cb.err(code, msg);
            }

            private WishApp.RequestCb init(Identity.FriendRequestDeclineCb callback) {
                this.cb = callback;
                return this;
            }
        }.init(callback);

        if (connection != null) {
            return wishApp.request.ConnectionRequest.request(connection, op, array, requestCb);
        } else {
            return WishApp.getInstance().request(buffer.toByteArray(), requestCb);
        }

    }
}


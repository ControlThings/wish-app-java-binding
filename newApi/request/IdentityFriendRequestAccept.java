package wishApp.newApi.request;

import org.bson.BSONException;
import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import bson.BsonExtendedBinaryWriter;
import bson.BsonExtendedWriter;
import wishApp.Connection;
import wishApp.WishApp;

import static wishApp.newApi.request.Callback.BSON_ERROR_CODE;
import static wishApp.newApi.request.Callback.BSON_ERROR_STRING;


class IdentityFriendRequestAccept {
    static int request(Connection connection, byte[] luid, byte[] ruid, Identity.FriendRequestAcceptCb callback) {
        final String op = "identity.friendRequestAccept";

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
            Identity.FriendRequestAcceptCb cb;

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

            private WishApp.RequestCb init(Identity.FriendRequestAcceptCb callback) {
                this.cb = callback;
                return this;
            }
        }.init(callback);

    if (connection != null) {
        return ConnectionRequest.request(connection, op, array, requestCb);
    } else {
        return WishApp.getInstance().request(buffer.toByteArray(), requestCb);
    }

    }
}


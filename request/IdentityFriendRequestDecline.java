package wish.request;

import org.bson.BSONException;
import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;


import utils.bson.BsonExtendedBinaryWriter;
import utils.bson.BsonExtendedWriter;
import wish.Connection;
import wish.WishApp;

import static wish.request.Callback.BSON_ERROR_CODE;
import static wish.request.Callback.BSON_ERROR_STRING;

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
                boolean value;
                try {
                    BsonDocument bson = new RawBsonDocument(data);
                    value = bson.getBoolean("data").getValue();
                } catch (BSONException e) {
                    cb.err(BSON_ERROR_CODE, BSON_ERROR_STRING);
                    return;
                }
                cb.cb(value);
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
            return wish.request.ConnectionRequest.request(connection, op, array, requestCb);
        } else {
            return WishApp.getInstance().request(buffer.toByteArray(), requestCb);
        }

    }
}


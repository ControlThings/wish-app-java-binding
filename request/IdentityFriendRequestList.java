package wish.request;

import org.bson.BSONException;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import java.util.ArrayList;
import java.util.List;


import utils.bson.BsonExtendedBinaryWriter;
import utils.bson.BsonExtendedWriter;
import wish.Connection;
import wish.WishApp;
import wish.Request;

import static wish.request.Callback.BSON_ERROR_CODE;
import static wish.request.Callback.BSON_ERROR_STRING;

class IdentityFriendRequestList {
    static int request(Connection connection, Identity.FriendRequestListCb callback) {
        final String op = "identity.friendRequestList";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonExtendedWriter writer = new BsonExtendedBinaryWriter(buffer);
        writer.writeStartDocument();

        writer.writeString("op", op);

        writer.writeStartArray("args");
        writer.writeEndArray();

        writer.writeInt32("id", 0);

        writer.writeEndDocument();
        writer.flush();

        WishApp.RequestCb requestCb = new WishApp.RequestCb() {
            Identity.FriendRequestListCb cb;

            @Override
            public void response(byte[] data) {
                List<Request> requests;
                try {
                    BsonDocument bson = new RawBsonDocument(data);
                    requests = new ArrayList<>();
                    BsonArray bsonArray = new BsonArray(bson.getArray("data"));
                    for (BsonValue listValue : bsonArray) {
                        Request request = new Request();
                        BsonDocument document = listValue.asDocument();
                        request.setLuid(document.get("luid").asBinary().getData());
                        request.setRuid(document.get("ruid").asBinary().getData());
                        request.setAlias(document.get("alias").asString().getValue());
                        request.setPubkey(document.get("pubkey").asBinary().getData());

                        if (document.containsKey("meta")) {
                            request.setMeta(document.getDocument("meta").asDocument());
                        }

                        requests.add(request);
                    }
                } catch (BSONException e) {
                    cb.err(BSON_ERROR_CODE, BSON_ERROR_STRING);
                    return;
                }
                cb.cb(requests);
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

            private WishApp.RequestCb init(Identity.FriendRequestListCb callback) {
                this.cb = callback;
                return this;
            }
        }.init(callback);

        if (connection != null) {
            return wish.request.ConnectionRequest.request(connection, op, new BsonArray(), requestCb);
        } else {
            return WishApp.getInstance().request(buffer.toByteArray(), requestCb);
        }
    }
}


package wish.request;

import org.bson.BSONException;
import org.bson.BsonArray;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import wish.Connection;
import wish.WishApp;

import static wish.request.Callback.BSON_ERROR_CODE;
import static wish.request.Callback.BSON_ERROR_STRING;

/**
 * Created by jeppe on 11/30/16.
 */

class WishSignals {
    static int request(Connection connection, Wish.SignalsCb callback) {
        final String op = "signals";


        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();

        writer.writeString("op", op);

        writer.writeStartArray("args");
        writer.writeEndArray();

        writer.writeInt32("id", 0);

        writer.writeEndDocument();
        writer.flush();

        WishApp.RequestCb requestCb = new WishApp.RequestCb() {
            Wish.SignalsCb cb;

            @Override
            public void response(byte[] data) {
                try {
                    BsonDocument bson = new RawBsonDocument(data);
                    String signalData;
                    if (bson.isString("data")) {
                        cb.cb(bson.getString("data").getValue());
                    } else if (bson.isArray("data")) {
                        BsonArray bsonArray = bson.getArray("data");
                        cb.cb(bsonArray.get(0).asString().getValue());
                    } else {
                        cb.cb(null);
                    }
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

            private WishApp.RequestCb init(Wish.SignalsCb callback) {
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

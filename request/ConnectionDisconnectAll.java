package wish.request;

import android.util.Log;

import org.bson.BSONException;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import wish.LocalDiscovery;
import wish.WishApp;

import static wish.request.Callback.BSON_ERROR_CODE;
import static wish.request.Callback.BSON_ERROR_STRING;

/**
 * Created by jeppe on 10/18/16.
 */
class ConnectionDisconnectAll {
    static int request(Connection.DisconnectAllCb callback) {
        String op = "connections.disconnectAll";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();

        writer.writeString("op", op);

        writer.writeStartArray("args");
        writer.writeEndArray();

        writer.writeInt32("id", 0);

        writer.writeEndDocument();
        writer.flush();

        return WishApp.getInstance().request(buffer.toByteArray(), new WishApp.RequestCb() {
            Connection.DisconnectAllCb cb;

            @Override
            public void response(byte[] data) {
                boolean value;
                try {

                    BsonDocument bson = new RawBsonDocument(data);
                    Log.d("disconnectAll", "bson: " + bson.toJson());
                    if (!bson.isBoolean("data")) {
                        cb.err(BSON_ERROR_CODE, BSON_ERROR_STRING);
                        return;
                    }
                    value = bson.get("data").asBoolean().getValue();

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

            private WishApp.RequestCb init(Connection.DisconnectAllCb callback) {
                this.cb = callback;
                return this;
            }

        }.init(callback));
    }
}

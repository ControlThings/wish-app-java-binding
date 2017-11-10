package wishApp.request;

import android.util.Log;

import org.bson.BSONException;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import wishApp.Errors;
import wishApp.RequestInterface;

import static wishApp.RequestInterface.bsonException;

/**
 * Created by jeppe on 9/28/16.
 */
class WldListFriendRequests {
    static void request(Wld.ListFriendRequestsCb callback) {
        final String op = "wld.listFriendRequests";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();
        writer.writeStartArray("args");
        writer.writeEndArray();
        writer.writeEndDocument();
        writer.flush();

        RequestInterface.getInstance().wishRequest(op, buffer.toByteArray(), new RequestInterface.Callback() {
            private Wld.ListFriendRequestsCb callback;

            @Override
            public void ack(byte[] dataBson) {
                response(dataBson);
                callback.end();
            }

            @Override
            public void sig(byte[] dataBson) {
                response(dataBson);
            }

            private void response(byte[] dataBson) {
                try {
                    BsonDocument bson = new RawBsonDocument(dataBson);
                    callback.cb(bson.get("data").asDocument());
                } catch (BSONException e) {
                    callback.err(bsonException, "bson error: " + e.getMessage());
                }
            }

            @Override
            public void err(int code, String msg) {
                callback.err(code, msg);
            }

            private RequestInterface.Callback init(Wld.ListFriendRequestsCb callback) {
                this.callback = callback;
                return this;
            }
        }.init(callback));
    }

}

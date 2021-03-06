package wish.request;

import org.bson.BSONException;
import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import utils.bson.BsonExtendedBinaryWriter;
import utils.bson.BsonExtendedWriter;
import wish.*;

import static wish.request.Callback.BSON_ERROR_CODE;
import static wish.request.Callback.BSON_ERROR_STRING;



class IdentityRemove {
    static int request(wish.Connection connection, byte[] uid, Identity.RemoveCb callback) {
        final String op = "identity.remove";

        BsonArray array = new BsonArray();
        array.add(new BsonBinary(uid));

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
            Identity.RemoveCb cb;

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

            private WishApp.RequestCb init(Identity.RemoveCb callback) {
                this.cb = callback;
                return this;
            }

        }.init(callback);

        if(connection != null) {
            return ConnectionRequest.request(connection, op, array, requestCb);
        } else {
            return WishApp.getInstance().request(buffer.toByteArray(), requestCb);
        }

    }
}

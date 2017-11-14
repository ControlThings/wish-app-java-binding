package wishApp.newApi.request;

import org.bson.BSONException;
import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import bson.BsonExtendedBinaryWriter;
import bson.BsonExtendedWriter;
import wishApp.Connection;
import wishApp.WishApp;

import static wishApp.newApi.request.Callback.BSON_ERROR_CODE;
import static wishApp.newApi.request.Callback.BSON_ERROR_STRING;

/**
 * Created by jeppe on 11/28/16.
 */

class IdentitySign {
    static int request(Connection connection, byte[] uid, BsonDocument cert, Identity.SignCb callback) {
        String op = "identity.sign";

        BsonArray array = new BsonArray();
        array.add(new BsonBinary(uid));
        array.add(cert);

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
            Identity.SignCb cb;

            @Override
            public void response(byte[] data) {
                try {
                    BsonDocument bson = new RawBsonDocument(data);
                    BsonDocument bsonData = bson.getDocument("data");

                    BsonDocumentReader reader = new BsonDocumentReader(bsonData);
                    BasicOutputBuffer buffer = new BasicOutputBuffer();
                    BsonWriter writer = new BsonBinaryWriter(buffer);
                    writer.pipe(reader);
                    writer.flush();

                    cb.cb(buffer.toByteArray());
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

            private WishApp.RequestCb init(Identity.SignCb calback) {
                this.cb = calback;
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

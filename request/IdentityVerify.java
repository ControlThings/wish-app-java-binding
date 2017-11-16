package wishApp.request;

import org.bson.BSONException;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import bson.BsonExtendedBinaryWriter;
import bson.BsonExtendedWriter;
import wishApp.Cert;
import wishApp.WishApp;

import static wishApp.request.Callback.BSON_ERROR_CODE;
import static wishApp.request.Callback.BSON_ERROR_STRING;

/**
 * Created by jeppe on 11/28/16.
 */

class IdentityVerify {
    static int request(Cert cert, Identity.VerifyCb callback) {
        String op = "identity.verify";

        BsonArray array = new BsonArray();
        array.add(cert.getCert());


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

       return WishApp.getInstance().request(buffer.toByteArray(), new WishApp.RequestCb() {
            Identity.VerifyCb cb;

            @Override
            public void response(byte[] data) {
                try {
                    BsonDocument bson = new RawBsonDocument(data);
                    BsonDocument bsonData = bson.getDocument("data");
                    boolean value = bsonData.get("data").asBoolean().getValue();
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

            private WishApp.RequestCb init(Identity.VerifyCb callback) {
                this.cb = callback;
                return this;
            }

        }.init(callback));
    }
}

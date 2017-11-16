package wishApp.request;

import org.bson.BSONException;
import org.bson.BsonBinary;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import wishApp.WishApp;

import static wishApp.request.Callback.BSON_ERROR_CODE;
import static wishApp.request.Callback.BSON_ERROR_STRING;

class IdentityImport {
    static int request(byte[] identity, Identity.ImportCb callback) {
        final String op = "identity.import";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();

        writer.writeString("op", op);

        writer.writeStartArray("args");
        writer.writeBinaryData(new BsonBinary(identity));
        writer.writeEndArray();

        writer.writeInt32("id", 0);

        writer.writeEndDocument();
        writer.flush();

        return WishApp.getInstance().request(buffer.toByteArray(), new WishApp.RequestCb() {
            Identity.ImportCb cb;

            @Override
            public void response(byte[] data) {
                try {
                    BsonDocument bson = new RawBsonDocument(data);
                    BsonDocument bsonDocument = bson.getDocument("data");

                    String alias = bsonDocument.getString("alias").getValue();
                    byte[] uid = bsonDocument.getBinary("uid").getData();

                    cb.cb(alias, uid);
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

            private WishApp.RequestCb init(Identity.ImportCb callback) {
                this.cb = callback;
                return this;
            }

        }.init(callback));
    }
}

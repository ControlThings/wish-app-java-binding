package wishApp.newApi.request;


import org.bson.BSONException;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import wishApp.WishApp;
import static wishApp.newApi.request.Callback.BSON_ERROR_CODE;
import static wishApp.newApi.request.Callback.BSON_ERROR_STRING;

/**
 * Created by jeppe on 8/23/16.
 */
class IdentityCreate {
    static int request(String alias, Identity.CreateCb callback) {
        final String op = "identity.create";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();

        writer.writeString("op", op);

        writer.writeStartArray("args");
        writer.writeString(alias);
        writer.writeEndArray();

        writer.writeInt32("id", 0);

        writer.writeEndDocument();
        writer.flush();

        return WishApp.getInstance().request(buffer.toByteArray(), new WishApp.RequestCb() {
            Identity.CreateCb cb;

            @Override
            public void response(byte[] data) {
                try {
                    BsonDocument bson = new RawBsonDocument(data);
                    BsonDocument bsonDocument = bson.getDocument("data");
                    wishApp.newApi.Identity identity = wishApp.newApi.Identity.fromBson(bsonDocument);
                    cb.cb(identity);
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
                cb.err(code, msg);
            }

            private WishApp.RequestCb init(Identity.CreateCb callback) {
                this.cb = callback;
                return this;
            }

        }.init(callback));
    }
}

package wish.request;

import org.bson.BSONException;
import org.bson.BsonBinary;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

<<<<<<< HEAD
import wishApp.WishApp;

import static wishApp.request.Callback.BSON_ERROR_CODE;
import static wishApp.request.Callback.BSON_ERROR_STRING;

=======
import wish.MistIdentity;
import wish.Errors;
import wish.RequestInterface;

import static wish.RequestInterface.bsonException;
>>>>>>> 6fcd683c362d9bebffbebfdf4fcd9fa28425ffd8

class IdentityGet {
    static int request(byte[] uid, Identity.GetCb callback) {
        final String op = "identity.get";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();

        writer.writeString("op", op);

        writer.writeStartArray("args");
        writer.writeBinaryData(new BsonBinary(uid));
        writer.writeEndArray();

        writer.writeInt32("id", 0);

        writer.writeEndDocument();
        writer.flush();

        return WishApp.getInstance().request(buffer.toByteArray(), new WishApp.RequestCb() {
            Identity.GetCb cb;

            @Override
            public void response(byte[] data) {
                try {
                    BsonDocument bson = new RawBsonDocument(data);
                    BsonDocument bsonDocument = bson.getDocument("data");
                    wishApp.Identity identity = wishApp.Identity.fromBson(bsonDocument);
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
                super.err(code, msg);
                cb.err(code, msg);
            }

            private WishApp.RequestCb init(Identity.GetCb callback) {
                this.cb = callback;
                return this;
            }

        }.init(callback));
    }
}

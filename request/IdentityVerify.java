package wishApp.request;

import android.util.Log;

import org.bson.BSONException;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import wishApp.Cert;
import wishApp.Errors;
import wishApp.RequestInterface;

import static wishApp.RequestInterface.bsonException;

/**
 * Created by jeppe on 11/28/16.
 */

class IdentityVerify {
    static void request(Cert cert, Identity.VerifyCb callback) {
        final String op = "identity.verify";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        BsonDocumentReader bsonDocumentReader = new BsonDocumentReader(cert.getCert());
        writer.pipe(bsonDocumentReader);
        writer.flush();

        RequestInterface.getInstance().wishRequest(op, buffer.toByteArray(), new RequestInterface.Callback() {
            private Identity.VerifyCb callback;

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
                    BsonDocument bsonData = bson.getDocument("data");
                    boolean data = bsonData.get("data").asBoolean().getValue();
                    callback.cb(data);
                } catch (BSONException e) {
                    callback.err(bsonException, "bson error: " + e.getMessage());
                }
            }

            @Override
            public void err(int code, String msg) {
                callback.err(code, msg);
            }

            private RequestInterface.Callback init(Identity.VerifyCb callback) {
                this.callback = callback;
                return this;
            }
        }.init(callback));
    }
}

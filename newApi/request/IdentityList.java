package wishApp.newApi.request;

import org.bson.BSONException;
import org.bson.BsonArray;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import java.util.ArrayList;
import java.util.List;

import wishApp.WishApp;

class IdentityList {
    static int request(Identity.ListCb callback) {
        String op = "identity.list";


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
            Identity.ListCb cb;

            @Override
            public void response(byte[] data) {
                try {
                    BsonDocument bson = new RawBsonDocument(data);
                    BsonArray bsonList = bson.getArray("data");
                    List<wishApp.newApi.Identity> list = new ArrayList<wishApp.newApi.Identity>();
                    for (BsonValue bsonIdentity : bsonList) {
                        list.add(wishApp.newApi.Identity.fromBson(bsonIdentity.asDocument()));
                    }
                    cb.cb(list);
                } catch (BSONException e) {
                    cb.err(Callback.BSON_ERROR_CODE, Callback.BSON_ERROR_STRING);
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

            private WishApp.RequestCb init(Identity.ListCb callback) {
                this.cb = callback;
                return this;
            }

        }.init(callback));
    }
}

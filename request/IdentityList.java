package wish.request;

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

import wish.*;

class IdentityList {
    static int request(wish.Connection connection, Identity.ListCb callback) {
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

        WishApp.RequestCb requestCb =  new WishApp.RequestCb() {
            Identity.ListCb cb;

            @Override
            public void response(byte[] data) {
                List<wish.Identity> list;
                try {
                    BsonDocument bson = new RawBsonDocument(data);
                    BsonArray bsonList = bson.getArray("data");
                    list = new ArrayList<wish.Identity>();
                    for (BsonValue bsonIdentity : bsonList) {
                        list.add(wish.Identity.fromBson(bsonIdentity.asDocument()));
                    }
                } catch (BSONException e) {
                    cb.err(wish.request.Callback.BSON_ERROR_CODE, wish.request.Callback.BSON_ERROR_STRING);
                    return;
                }
                cb.cb(list);
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

            private WishApp.RequestCb init(Identity.ListCb callback) {
                this.cb = callback;
                return this;
            }

        }.init(callback);

        if (connection != null) {
            return wish.request.ConnectionRequest.request(connection, op, new BsonArray(), requestCb);
        } else {
            return WishApp.getInstance().request(buffer.toByteArray(),requestCb);
        }

    }
}

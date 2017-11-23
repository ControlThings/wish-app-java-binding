package wish.request;

import android.util.Log;

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

import wish.LocalDiscovery;
import wish.WishApp;

import static wish.request.Callback.BSON_ERROR_CODE;
import static wish.request.Callback.BSON_ERROR_STRING;

/**
 * Created by jeppe on 9/28/16.
 */
class WldList {
    static int request(Wld.ListCb callback) {
        final String op = "wld.list";

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
            Wld.ListCb cb;

            @Override
            public void response(byte[] data) {
                try {
                    BsonDocument bson = new RawBsonDocument(data);
                    BsonArray bsonArray = bson.get("data").asArray();
                    List<LocalDiscovery> localDiscoveries = new ArrayList<LocalDiscovery>();

                    for (BsonValue listValue : bsonArray) {
                        LocalDiscovery localDiscovery = new LocalDiscovery();
                        localDiscovery.setType(listValue.asDocument().get("type").asString().getValue());
                        localDiscovery.setAlias(listValue.asDocument().get("alias").asString().getValue());
                        if (listValue.asDocument().containsKey("luid")) {
                            localDiscovery.setLuid(listValue.asDocument().get("luid").asBinary().getData());
                        }
                        localDiscovery.setRuid(listValue.asDocument().get("ruid").asBinary().getData());
                        localDiscovery.setRhid(listValue.asDocument().get("rhid").asBinary().getData());
                        localDiscovery.setPubkey(listValue.asDocument().get("pubkey").asBinary().getData());
                        if (listValue.asDocument().containsKey("claim")) {
                            localDiscovery.setClaim(listValue.asDocument().get("claim").asBoolean().getValue());
                        } else {
                            localDiscovery.setClaim(false);
                        }

                        localDiscoveries.add(localDiscovery);
                    }
                    cb.cb(localDiscoveries);
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

            private WishApp.RequestCb init(Wld.ListCb callback) {
                this.cb = callback;
                return this;
            }

        }.init(callback));
    }
}

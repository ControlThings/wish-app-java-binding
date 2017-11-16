package wishApp.request;

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

import static wishApp.request.Callback.BSON_ERROR_CODE;
import static wishApp.request.Callback.BSON_ERROR_STRING;


/**
 * Created by jeppe on 9/28/16.
 */
class ConnectionList {
    static int request(Connection.ListCb callback) {
        String op = "connections.list";

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
            Connection.ListCb cb;

            @Override
            public void response(byte[] data) {
                try {
                    BsonDocument bson = new RawBsonDocument(data);
                    List<wishApp.Connection> connections = new ArrayList<wishApp.Connection>();
                    BsonArray bsonArray = bson.get("data").asArray();
                    for (BsonValue bsonValue : bsonArray) {
                        wishApp.Connection connection = new wishApp.Connection();
                        connection.setCid(bsonValue.asDocument().get("cid").asInt32().getValue());
                        connection.setLuid(bsonValue.asDocument().get("luid").asBinary().getData());
                        connection.setRuid(bsonValue.asDocument().get("ruid").asBinary().getData());
                        connection.setRhid(bsonValue.asDocument().get("rhid").asBinary().getData());
                        connection.setOutgoing(bsonValue.asDocument().get("outgoing").asBoolean().getValue());
                        connections.add(connection);
                    }
                    cb.cb(connections);
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

            private WishApp.RequestCb init(Connection.ListCb callback) {
                this.cb = callback;
                return this;
            }

        }.init(callback));
    }
}

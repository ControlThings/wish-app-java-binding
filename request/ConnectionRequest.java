package wish.request;

import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.io.BasicOutputBuffer;


import utils.bson.BsonExtendedBinaryWriter;
import utils.bson.BsonExtendedWriter;
import wish.*;
import wish.request.Connection;

import static wish.WishApp.bsonConsolePrettyPrinter;

/**
 * Created by jeppe on 11/14/17.
 */

class ConnectionRequest {

    static int request(wish.Connection connection, String requestOp, BsonArray array, Connection.RequestCb callback) {
        return send(connection, requestOp, array, callback, null);
    }

    static int request(wish.Connection connection, String requestOp, BsonArray array, WishApp.RequestCb requestCb) {
        return send(connection, requestOp, array, null, requestCb);
    }

    private static int send(wish.Connection connection, String requestOp, BsonArray array, Connection.RequestCb callback, WishApp.RequestCb requestCb) {
        final String op = "connections.request";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonExtendedWriter writer = new BsonExtendedBinaryWriter(buffer);
        writer.writeStartDocument();

        writer.writeString("op", op);

        writer.writeStartArray("args");

        writer.writeStartDocument();
        writer.writeBinaryData("luid", new BsonBinary(connection.getLuid()));
        writer.writeBinaryData("ruid", new BsonBinary(connection.getRuid()));
        writer.writeBinaryData("rhid", new BsonBinary(connection.getRhid()));
        writer.writeEndDocument();

        writer.writeString(requestOp);

        writer.writeStartArray();
        writer.pipeArray(array);
        writer.writeEndArray();

        writer.writeEndArray();

        writer.writeInt32("id", 0);

        writer.writeEndDocument();
        writer.flush();

        if (requestCb != null) {
            return WishApp.getInstance().request(buffer.toByteArray(), requestCb);
        } else {
            return WishApp.getInstance().request(buffer.toByteArray(), new WishApp.RequestCb() {
                Connection.RequestCb cb;

                @Override
                public void response(byte[] data) {
                    cb.cb(data);
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

                private WishApp.RequestCb init(Connection.RequestCb callback) {
                    this.cb = callback;
                    return this;
                }

            }.init(callback));
        }


    }


}

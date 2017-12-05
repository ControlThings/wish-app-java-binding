package wish.request;

import org.bson.BSONException;
import org.bson.BsonBinary;
import org.bson.BsonBinaryWriter;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;

import java.util.Arrays;
import java.util.List;

import wish.Cert;
import wish.Peer;
import wish.WishApp;

import static wish.request.Callback.BSON_ERROR_CODE;
import static wish.request.Callback.BSON_ERROR_STRING;

/**
 * Created by jeppe on 9/28/16.
 */
class IdentityFriendRequest {
    static void request(byte[] luid, BsonDocument contact, BsonDocument document, Identity.FriendRequestCb callback) {
        final String op = "identity.friendRequest";

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        writer.writeStartDocument();

        writer.writeString("op", op);

        writer.writeStartArray("args");

        writer.writeBinaryData(new BsonBinary(luid));

        try {
            if (contact.containsKey("data") && contact.containsKey("meta")) {
                writer.writeStartDocument();
                writer.writeBinaryData("data", contact.getBinary("data"));
                writer.writeBinaryData("meta", contact.getBinary("meta"));
                writer.writeEndDocument();
            } else {
                callback.err(757, "invalide contact");
                return;
            }
        } catch (BSONException e) {
            callback.err(757, e.getMessage());
            return;
        }

        if (document != null) {
            writer.pipe(new BsonDocumentReader(document));
        }

        writer.writeEndArray();

        writer.writeInt32("id", 0);

        writer.writeEndDocument();
        writer.flush();

        WishApp.getInstance().request(buffer.toByteArray(), new WishApp.RequestCb() {
            Identity.FriendRequestCb cb;

            @Override
            public void response(byte[] data) {
                boolean state;
                try {
                    BsonDocument bson = new RawBsonDocument(data);
                    state = bson.get("data").asBoolean().getValue();
                } catch (BSONException e) {
                    cb.err(BSON_ERROR_CODE, BSON_ERROR_STRING);
                    return;
                }
                cb.cb(state);
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

            private WishApp.RequestCb init(Identity.FriendRequestCb callback) {
                this.cb = callback;
                return this;
            }
        }.init(callback));
    }


    static void requestDocument(final byte[] luid, final BsonDocument contact, final Peer peer, Identity.FriendRequestCb callback) {
        Identity.export(peer.getRuid(), new Identity.ExportCb() {

            private Identity.FriendRequestCb callback;

            @Override
            public void cb(byte[] data, byte[] raw) {

                final BasicOutputBuffer dataBuffer = new BasicOutputBuffer();
                BsonWriter dataWriter = new BsonBinaryWriter(dataBuffer);
                dataWriter.writeStartDocument();
                dataWriter.writeString("alias", new RawBsonDocument(data).getString("alias").getValue());
                dataWriter.writeBinaryData("uid", new BsonBinary(peer.getRuid()));
                dataWriter.writeBinaryData("hid", new BsonBinary(peer.getRhid()));
                dataWriter.writeBinaryData("sid", new BsonBinary(peer.getRsid()));
                dataWriter.writeBinaryData("pubkey", new RawBsonDocument(data).getBinary("pubkey"));
                dataWriter.writeEndDocument();
                dataWriter.flush();

                BasicOutputBuffer buffer = new BasicOutputBuffer();
                BsonWriter writer = new BsonBinaryWriter(buffer);
                writer.writeStartDocument();
                writer.writeBinaryData("data", new BsonBinary(dataBuffer.toByteArray()));
                writer.writeBinaryData("meta", new RawBsonDocument(raw).getDocument("data").getBinary("meta"));
                writer.writeEndDocument();
                writer.flush();

                byte[] cert = buffer.toByteArray();

                Connection.list(new Connection.ListCb() {
                    private Identity.FriendRequestCb callback;
                    private byte[] cert;

                    @Override
                    public void cb(List<wish.Connection> connections) {
                        for (wish.Connection connection : connections) {
                            if (Arrays.equals(connection.getLuid(), peer.getLuid()) && Arrays.equals(connection.getRhid(), peer.getRhid()) && Arrays.equals(connection.getRuid(), peer.getRuid())) {

                                Identity.sign(connection, peer.getRuid(), new RawBsonDocument(cert), new Identity.SignCb() {

                                    private Identity.FriendRequestCb callback;

                                    @Override
                                    public void cb(byte[] data) {
                                        Cert cert = new Cert();
                                        cert.setCert(new RawBsonDocument(data));
                                        request(luid, contact, new RawBsonDocument(data), callback);
                                    }

                                    @Override
                                    public void err(int code, String msg) {
                                        callback.err(code, msg);
                                    }

                                    @Override
                                    public void end() {
                                    }

                                    private Identity.SignCb init(Identity.FriendRequestCb callback) {
                                        this.callback = callback;
                                        return this;
                                    }
                                }.init(callback));

                            }
                        }
                    }

                    @Override
                    public void err(int code, String msg) {
                        callback.err(code, msg);
                    }

                    @Override
                    public void end() {
                    }

                    private Connection.ListCb init(Identity.FriendRequestCb callback, byte[] cert) {
                        this.callback = callback;
                        this.cert = cert;
                        return this;
                    }
                }.init(callback, cert));


            }

            @Override
            public void err(int code, String msg) {
                callback.err(code, msg);
            }

            @Override
            public void end() {
            }

            private Identity.ExportCb init(Identity.FriendRequestCb callback) {
                this.callback = callback;
                return this;
            }

        }.init(callback));
    }

}

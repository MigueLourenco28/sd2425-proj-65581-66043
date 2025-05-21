package server.grpc;

import client.ContentClient;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import network.ServiceAnnouncer;

import javax.net.ssl.KeyManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Optional;
import java.util.logging.Logger;

public class GrpcContentServer {

    private static final Logger log = Logger.getLogger(GrpcContentServer.class.getName());

    public static final int PORT = 9000;

    public static void main(String[] args) {
        launchServer(PORT);
    }

    public static void launchServer(int port) {
        launchServer(port, Optional.empty());
    }

    public static void launchServer(int port, long period) {
        launchServer(port, Optional.of(period));
    }

    public static void launchServer(int port, Optional<Long> period) {
        try {
            var serverURI = GrpcServerUtils.computeServerUri(port);
            announceService(serverURI, period);
            var stub = new GrpcContentStub(serverURI);

            //TLS

            String keyStoreFilename = System.getProperty("javax.net.ssl.keyStore");
            String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            try (FileInputStream input = new FileInputStream(keyStoreFilename)) {
                keyStore.load(input, keyStorePassword.toCharArray());
            }

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

            SslContext sslContext = SslContextBuilder.forServer(keyManagerFactory).build();

            Server server = NettyServerBuilder.forPort(port)
                    .sslContext(sslContext)
                    .addService(stub)
                    .build()
                    .start();

            log.info(String.format("Content gRPC Server with TLS ready @ %s\n", serverURI));
            server.awaitTermination();

        } catch (Exception e) {
            log.severe("Unable to launch gRPC server at port %d".formatted(port));
            throw new RuntimeException(e);
        }
    }

    private static void announceService(String serverURI, Optional<Long> period) throws IOException {
        if (period.isPresent())
            new ServiceAnnouncer(ContentClient.SERVICE, serverURI, period.get());
        else
            new ServiceAnnouncer(ContentClient.SERVICE, serverURI);
    }
}
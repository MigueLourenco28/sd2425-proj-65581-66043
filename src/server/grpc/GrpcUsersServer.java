package server.grpc;

import client.UsersClient;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import network.ServiceAnnouncer;

import javax.net.ssl.KeyManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Optional;
import java.util.logging.Logger;

public class GrpcUsersServer {

    private static final Logger log = Logger.getLogger(GrpcUsersServer.class.getName());

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

    private static void launchServer(int port, Optional<Long> period) {
        try {
            var serverURI = GrpcServerUtils.computeServerUri(port);
            announceService(serverURI, period);
            var stub = new GrpcUsersStub();

            //TLS

            String keyStoreFilename = System.getProperty("javax.net.ssl.keyStore");
            String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");

            // Load the keystore
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());

            try (FileInputStream input = new FileInputStream(keyStoreFilename)) {
                keystore.load(input, keyStorePassword.toCharArray());
            }

            // Initialize key manager factory with keystore
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, keyStorePassword.toCharArray());

            // Create the SSL context
            SslContext sslContext = SslContextBuilder.forServer(keyManagerFactory).build();

            // Launch the server using Netty and SSL context
            Server server = NettyServerBuilder.forPort(port)
                    .sslContext(sslContext)
                    .addService(stub)
                    .build()
                    .start();

            log.info(String.format("Users gRPC Server with TLS ready @ %s\n", serverURI));
            server.awaitTermination();

        } catch (Exception e) {
            log.severe("Unable to launch gRPC server at port %d".formatted(port));
            throw new RuntimeException(e);
        }
    }

    private static void announceService(String serverURI, Optional<Long> period) throws Exception {
        if (period.isPresent())
            new ServiceAnnouncer(UsersClient.SERVICE, serverURI, period.get());
        else
            new ServiceAnnouncer(UsersClient.SERVICE, serverURI);
    }
}

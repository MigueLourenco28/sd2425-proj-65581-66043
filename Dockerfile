FROM smduarte/sd2425testerbase

# working directory inside docker image
WORKDIR /home/sd

ADD hibernate.cfg.xml .
ADD fctreddit.props .

# copy hibernate config
COPY hibernate.cfg.xml hibernate.cfg.xml

# copy keystore and truststore
COPY *.ks /home/sd/

# copy the jar created by assembly to the docker image
COPY target/*jar-with-dependencies.jar sd2425.jar

# run Discovery when starting the docker image
CMD ["java", "-cp", "sd2425.jar", "-Djavax.net.ssl.keyStore=/home/sd/users-server.ks" , "-Djavax.net.ssl.keyStorePassword=password" , "-Djavax.net.ssl.trustStore=/home/sd/truststore.ks" , "-Djavax.net.ssl.trustStorePassword=changeit" , "server.grpc.GrpcUsersServer"]

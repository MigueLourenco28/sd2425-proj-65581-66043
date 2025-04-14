FROM smduarte/sd2425testerbase

# working directory inside docker image
WORKDIR /home/sd

ADD hibernate.cfg.xml .
ADD fctreddit.props .

# copy storage directory with the default avatar image
#COPY  imageFiles imageFiles

# copy an example image for experiments
#COPY example.png example.png

# copy the jar created by assembly to the docker image
COPY target/*jar-with-dependencies.jar sd2425.jar

# run Discovery when starting the docker image
CMD ["java", "-cp", "sd2425.jar", "fctreddit.impl.server.rest.UsersServer"]
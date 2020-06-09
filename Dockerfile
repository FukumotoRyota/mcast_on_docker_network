FROM openjdk:14
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
RUN javac MulticastClient.java
CMD ["/bin/bash"]
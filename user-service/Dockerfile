FROM openjdk:11 

ADD ./target/user-service-1.0.0-SNAPSHOT.jar /usr/src/user-service-1.0.0-SNAPSHOT.jar  

WORKDIR /usr/src  

ENTRYPOINT ["java","-jar","user-service-1.0.0-SNAPSHOT.jar"]



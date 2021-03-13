FROM openjdk:11 

ADD ./target/configuration-service-1.0.0-SNAPSHOT.jar /usr/src/configuration-service-1.0.0-SNAPSHOT.jar  

WORKDIR /usr/src  

ENTRYPOINT ["java","-jar","configuration-service-1.0.0-SNAPSHOT.jar"]



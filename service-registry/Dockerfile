FROM openjdk:11 

ADD ./target/service-registry-1.0.0-SNAPSHOT.jar /usr/src/service-registry-1.0.0-SNAPSHOT.jar  

WORKDIR /usr/src  

ENTRYPOINT ["java","-jar","service-registry-1.0.0-SNAPSHOT.jar"]



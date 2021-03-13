# Organic World Case study - Implementing Microservices patterns

## Problem Statement

Organic World has started business in 2017 as an organic food store. It started in a 100 sq. ft. store at Detroit. 
Due to its high quality products, reasonable price and steady supply, the store has gained popularity and currently
operates with twenty outlets in Detroit. Organic World has plans to open multiple stores across the country. 


To cater to its growing business, Organic World has digitized their operation. With the increase in traffic for their solution, they have increasing problems with operations of their systems in production. They have decided to migrate from a traditional VM based solution to a container based platform, enabling their developers to continue to build systems that react to varying workloads with a nimble Site Reliability Engineering team. 

## Proposed Solution
In order to enable developers to build operational solutions, the technical leaders have chosen Kubernetes on the cloud
as a platform of their choice, with istio to help with Site Reliability Engineering.

## High Level Requirements

The high level requirements for the initial release of application backend are given below:

- Containerize the application and migrate it to k8s.
  - Create a new `k8s` folder and place all kubernetes manifests files in it
- Deploy the application on the cloud (EKS, AKS or GKE)
- Replace Netflix Zuul with istio:
  - Configure Ingress rules and routing with Istio
- Configure mTLS, authorization, rate limiting (10requests / 5s), timeouts (3s), retries (3), connection pooling (4 connections), and bulk heading
- Configure SSL Certificate for the Ingress Gateway
- Configure Authorization for HTTP traffic
- Configure CI/CD for the project
- Document Deployment steps in DEPLOYMENT.md

## Microservices

**Source codes for the below microservices has been already provided** 

- **user-service**: Responsible for managing customer profiles and authentication of customers 
- **product-service**: Responsible for searching products and providing a web interface to admin for managing products 
- **order-service**: Responsible for placing orders for products 

- **configuration-service**: Responsible for working as a centralized configuration server 
- **service-registry**: Responsible for registration and discovery of microservice instances
- **api-gateway-service**: Responsible for working as a gateway for all requests for all microservices and to authenticate the user(if required)

## Tech Stack

    - Java 11
    - Spring Boot 2.x
    - Spring Framework 5
    - REST 2.0 API
    - MySQL v8
    - MongoDB 4.x
    - Netflix Eureka
    - Netflix Zuul
    - Spring cloud config
    - JUnit 5 and Mockito
    - Kafka
    - JWT
    - slf4j
    - Kubernetes
    - Istio

### Important Information

#### Running the application locally after cloning
    > After implementing the requirements, execute the following maven command in the parent/root project

            mvn clean package
    
    > Start Redis, MySQL, Kafka, MongoDb. Instruction for installation and starting these are provided in below section

    > The Services have to be started in the following order
        - configuration-service
        - service-registry
        - api-gateway-service
        
        followed by the remaining services. 
    
     > Use the following maven command to start individual services from their respective folders
        
            mvn spring-boot:run
    
    Swagger Documentation for the microservies can be accessed at below urls:
     
        - http://localhost:8765/order-service/swagger-ui.html
        - http://localhost:8765/product-service/swagger-ui.html
        - http://localhost:8765/user-service/swagger-ui.html 
     
    Admin Dashboard can be accessed at below url 
     
        - http://localhost:9000/admin 
          (Default username: admin password: admin)
   
#### Following software needs to be available/installed in the local environment for development

**MongoDb**

    > Use the following commands on ubuntu terminal for installing MongoDb
            
        sudo apt update
        sudo apt install -y mongodb
            
    > Check the Server status using following command
                    
        sudo systemctl status mongodb
        
    > Use the following command to connect to MongoDb server using mongo cli
                
            mongo
            
**Kafka**

     > Refer the steps provided at the below url to install and start apache kafka
        
         https://kafka.apache.org/quickstart
    
**Redis Server**

      > Use the following commands on ubuntu terminal for installing Redis Server

            sudo apt update
            sudo apt install redis-server
        
      > Check the Server status using following command
            
            sudo systemctl status redis-server
        
      > Use the following command to connect to Redis server using redis cli
        
            redis-cli 
            
**MySQL Server**
      
      > Check whether Mysql is installed in the environment provided to you by running the below command in terminal
             
             mysql --version
      
      > If MySQL is not installed, Refer the steps provided at the below url to install and start MySQL Server.
        
            https://linuxize.com/post/how-to-install-mysql-on-ubuntu-18-04/
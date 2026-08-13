# 🏋️ mga-resource-crud-service

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4%2F4.0-6DB33F?logo=springboot)](https://spring.io/)
[![Java Version](https://img.shields.io/badge/Java-25-007396?logo=openjdk)](https://openjdk.org/)
[![Build Tool](https://img.shields.io/badge/Build-Gradle%209.4.1-066DA5?logo=gradle)](https://gradle.org/)
[![GraalVM](https://img.shields.io/badge/GraalVM-Native%20Image-orange?logo=graalvm)](https://www.graalvm.org/)
[![Database](https://img.shields.io/badge/Database-MongoDB-47A248?logo=mongodb)](https://www.mongodb.com/)
[![Network](https://img.shields.io/badge/Security-Cluster--Internal-red)](https://kubernetes.io/docs/concepts/services-networking/network-policies/)

## 📌 Service Overview

`mga-resource-crud-service` is a cluster-internal MongoDB data-access microservice responsible for persistence, schedule storage, equipment state updates, and session booking execution across resource domain entities.

It operates strictly beneath `mga-resource-service` in a CQRS/Data-Layer Separation pattern, firewalled behind Kubernetes NetworkPolicy.

### 🏛️ Architectural Features
* Firewalled CQRS Isolation: Completely firewalled from public ingress; invoked strictly via internal RestClient calls from mga-resource-service.
* GraalVM Native Image Packaging: Built Ahead-Of-Time (AOT) with Java 25 & Gradle 9.4.1 for sub-second startup and low memory footprint in K8s.
* Dedicated Persistence Layer: Direct access to isolated Resource MongoDB database for equipment, facility slots, and class schedules.

---

## 🛠️ Technical Specifications
* Port: 8081 (Cluster-Internal)
* Language/Build: Java 25 | Gradle 9.4.1
* Persistence: MongoDB Atlas / Compass (Resource DB)
* Shared Contract: mga-all-models-jars

---

## 📡 Core API Controller Endpoints

### Classes & Schedules Controller (`/crud/resources/classes`)
* `POST /crud/resources/classes` - Save/create new fitness class schedule
* `GET /crud/resources/classes/{id}` - Fetch class schedule details by ID
* `GET /crud/resources/classes/all` - List all upcoming gym class schedules
* `PUT /crud/resources/classes/{id}` - Update fitness class schedule or instructor details
* `DELETE /crud/resources/classes/{id}` - Cancel/delete class schedule by ID

### Bookings Controller (`/crud/resources/bookings`)
* `POST /crud/resources/bookings` - Save new class booking reservation
* `GET /crud/resources/bookings/{id}` - Fetch booking details by ID
* `GET /crud/resources/bookings/member/{memberId}` - List all active bookings for a specific member
* `DELETE /crud/resources/bookings/{id}` - Cancel class booking reservation by ID

### Equipment & Facilities Controller (`/crud/resources/equipment`)
* `POST /crud/resources/equipment` - Register new gym equipment/facility item
* `GET /crud/resources/equipment/all` - List all registered gym equipment
* `PUT /crud/resources/equipment/{id}/status` - Update operational/maintenance status of equipment
* `DELETE /crud/resources/equipment/{id}` - Remove equipment record from database

---

## ⚙️ Build & Deployment Instructions
# Standard JVM Run
./gradlew bootRun

# GraalVM Native Image Compilation
./gradlew nativeCompile

# Paketo OCI Native Container Image
./gradlew bootBuildImage

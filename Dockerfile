FROM openjdk:8-alpine

COPY target/uberjar/gilded-rose-2.jar /gilded-rose-2/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/gilded-rose-2/app.jar"]

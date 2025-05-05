# spring-cloud-stream-kafka-sample

## Docker compose

Download the docker compose for kafka components and Confluent Platform from
here: [Docker Compose](https://github.com/cjrequena/cp-all-in-one)

## How to use it without schema registry.

To use producers and consumers example without a schema registry

```yml
spring.cloud.stream.bindings.useNativeEncoding: false
```

- Remove from producer-properties

```yml
  producer-properties:
    key.serializer: org.apache.kafka.common.serialization.StringSerializer
    value.serializer: io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer
    schema.registry.url: http://localhost:8081
    auto.register.schemas: false
    use.latest.version: true
```   

- Remove from consumer-properties

```yml
  consumer-properties:
    key.deserializer: org.apache.kafka.common.serialization.StringDeserializer
    value.deserializer: io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer
    schema.registry.url: http://localhost:8081
    id.compatibility.strict: true
    json.fail.invalid.schema: false
    json.fail.unknown.properties: true
``` 
  


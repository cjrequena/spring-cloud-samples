package com.cjrequena.sample.configuration;

import com.cjrequena.sample.persistence.codec.OffsetDateTimeCodec;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCompressor;
import com.mongodb.reactivestreams.client.MongoClients;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.mongodb.MongoMetricsCommandListener;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.transaction.ReactiveTransactionManager;

import java.util.List;


@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.cjrequena.sample.persistence.repository")
public class MongoReactiveConfiguration extends AbstractReactiveMongoConfiguration {
    @Autowired
    private MeterRegistry meterRegistry;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;
    private String db;

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }

    @Override
    protected String getDatabaseName() {
        return db;
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        var connection = new ConnectionString(mongoUri);
        db = connection.getDatabase();
        builder.compressorList(List.of(MongoCompressor.createZlibCompressor()));
        builder.addCommandListener(new MongoMetricsCommandListener(meterRegistry));
        builder.applyConnectionString(connection);
        builder.codecRegistry(CodecRegistries.fromRegistries(CodecRegistries.fromCodecs(new OffsetDateTimeCodec()), MongoClients.getDefaultCodecRegistry(), CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())));
    }

    @Bean
    @ConditionalOnProperty(prefix = "mongodb.transaction", name = "enabled", havingValue = "true")
    ReactiveTransactionManager transactionManager(ReactiveMongoDatabaseFactory factory) {
        return new ReactiveMongoTransactionManager(factory);
    }

}

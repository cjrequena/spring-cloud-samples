package com.cjrequena.sample.db.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;


public class OffsetDateTimeCodec implements Codec<OffsetDateTime> {
    public static final String DATE_TIME = "dateTime";
    public static final String OFFSET = "offset";

    @Override
    public OffsetDateTime decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        long epochMilli = reader.readDateTime(DATE_TIME);
        String zoneOffsetId = reader.readString(OFFSET);
        reader.readEndDocument();

        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneOffset.of(zoneOffsetId));

    }

    @Override
    public void encode(BsonWriter writer, OffsetDateTime value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeDateTime(DATE_TIME, value.toInstant().toEpochMilli());
        writer.writeString(OFFSET, value.getOffset().getId());
        writer.writeEndDocument();
    }

    @Override
    public Class<OffsetDateTime> getEncoderClass() {
        return OffsetDateTime.class;
    }
}

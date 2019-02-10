package com.github.frtu.serdes.avro;

import org.apache.avro.Schema;
import org.apache.avro.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

//import org.apache.kafka.common.serialization.Deserializer;
//import org.apache.kafka.common.errors.SerializationException;

/**
 * Avro record deserializer.
 * <p>
 * NOTE : Can be used as Kafka Deserializer but the current library doesn't pollute the dependency with the fat JAR.
 * </p>
 *
 * @param <T> The specific Avro class it is meant to deserialize
 * @author frtu
 */
public abstract class AvroRecordDeserializer<T> { // implements Deserializer<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvroRecordDeserializer.class);

    private Schema schema;

    private boolean isFormatJson = false;

    protected AvroRecordDeserializer(Schema schema) {
        this(schema, false);
    }

    protected AvroRecordDeserializer(Schema schema, boolean isFormatJson) {
        this.schema = schema;
        this.isFormatJson = isFormatJson;
    }

    public Schema getSchema() {
        return schema;
    }

//  @Override
//  public T deserialize(String topic, byte[] bytes) {
//    try {
//      T record = deserialize(bytes);
//      return record;
//    } catch (IOException e) {
//      final String errMsg = String.format("Error when deserializing bytes {} due to {}", bytes, e.getMessage());
//      LOGGER.error(errMsg, e);
//      throw new SerializationException(errMsg, e);
//    }
//  }

    protected abstract DatumReader<T> buildDatumReader();

    public T deserialize(ByteBuffer byteBuffer) throws IOException {
        return deserialize(byteBuffer.array());
    }

    public T deserialize(byte[] bytes) throws IOException {
        return deserialize(bytes, this.isFormatJson);
    }

    public T deserialize(byte[] bytes, boolean isFormatJson) throws IOException {
        LOGGER.debug("Deserialize bytes:{}", bytes);
        DatumReader<T> datumReader = buildDatumReader();
        Decoder decoder;
        if (isFormatJson) {
            decoder = DecoderFactory.get().jsonDecoder(this.schema, new ByteArrayInputStream(bytes));
        } else {
            decoder = DecoderFactory.get().binaryDecoder(bytes, null);
        }
        T record = datumReader.read(null, decoder);
        LOGGER.debug("Deserialize successful:{}", record);
        return record;
    }

    //  @Override
    public void close() {
        // nothing to do
    }
}

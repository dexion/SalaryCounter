package com.snake.salarycounter.utils;

import com.activeandroid.serializer.TypeSerializer;

import org.joda.time.Duration;

public class JodaDurationSerializer extends TypeSerializer {

    public Class<?> getDeserializedType() {
        return Duration.class;
    }

    public Class<?> getSerializedType() {
        return long.class;
    }

    public Long serialize(Object data) {
        if (data == null) {
            return null;
        }

        return ((Duration) data).getMillis();
    }

    public Duration deserialize(Object data) {
        if (data == null) {
            return null;
        }

        return new Duration((Long) data);
    }
}

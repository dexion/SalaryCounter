package com.snake.salarycounter.utils;

import com.activeandroid.serializer.TypeSerializer;

import java.math.BigDecimal;

public class BigDecimalSerializer extends TypeSerializer {
    private final int PRECISION = 2;
    public Class<?> getDeserializedType() {
        return BigDecimal.class;
    }

    public Class<?> getSerializedType() {
        return long.class;
    }

    public Long serialize(Object data) {
        if (data == null) {
            return null;
        }

        return ((BigDecimal) data).scaleByPowerOfTen(PRECISION).longValue();
    }

    public BigDecimal deserialize(Object data) {
        if (data == null) {
            return null;
        }

        return (new BigDecimal((long)data)).scaleByPowerOfTen(PRECISION * -1);
    }
}

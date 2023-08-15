package io.haicheng.util;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * 通过BigDecimal序列化Double类型的Json字段
 */
public class BigDecimalForDoubleSerializer extends JsonSerializer<Double> {

    public static BigDecimalForDoubleSerializer SINGLETON = new BigDecimalForDoubleSerializer();

    private BigDecimalForDoubleSerializer() {
    }

    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeNumber(
                ObjectUtil.isNotNull(value) ? new BigDecimal(value).toPlainString() : BigDecimal.ZERO.toPlainString());
    }
}

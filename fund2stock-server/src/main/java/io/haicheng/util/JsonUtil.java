package io.haicheng.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setSerializationInclusion(Include.NON_NULL);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 序列化和反序列化的时候针对浮点类型使用BigDecimal转换，避免精度损失和科学计数法
        OBJECT_MAPPER.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Double.class, BigDecimalForDoubleSerializer.SINGLETON);
        OBJECT_MAPPER.registerModule(simpleModule);
        OBJECT_MAPPER.setSerializationInclusion(Include.NON_NULL);
    }

    public static String writeValueAsString(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    public static <T> T write2Object(String json, TypeReference<T> typeReference) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, typeReference);
    }

    public static <T> T writeValueAsObject(String json, Class<T> targetClass) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, targetClass);
    }
}

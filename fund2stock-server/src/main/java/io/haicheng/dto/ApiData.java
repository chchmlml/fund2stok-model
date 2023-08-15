package io.haicheng.dto;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiData {
    String content;
    List<Integer> listYear;
    String curYear;

    public static ApiData parse(String response) {
        JSONObject data = JSONUtil.parseObj(StrUtil.removePrefixIgnoreCase(response, "var apidata="));

        String content = data.getStr("content");
        List<Integer> listYear = data.getJSONArray("arryear").toBean(new TypeReference<List<Integer>>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });
        String curyear = data.getStr("curyear");

        return ApiData.builder().curYear(curyear).listYear(listYear).content(content).build();
    }
}

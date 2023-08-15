package io.haicheng.controller;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.haicheng.crawlers.FundCrawl2;
import io.haicheng.dto.ApiSeasonData;
import io.haicheng.dto.ApiSeasonStockData;
import io.haicheng.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class DefaultController {


    @RequestMapping(value = "/test", method = {RequestMethod.GET})
    public Object test() {

        return "everything is ok";
    }

    @RequestMapping(value = "/file/cal", method = {RequestMethod.GET})
    public Object calculateFileData() {

        List<String> file = FileUtil.readLines(FundCrawl2.path, Charset.defaultCharset());

        file.stream().map(l -> {
                    try {
                        return JsonUtil.writeValueAsObject(l, ApiSeasonData.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).sorted(Comparator.comparing(ApiSeasonData::getDate, (x, y) -> CompareUtil.compare(x, y)))
                .reduce(ApiSeasonData.builder().items(ListUtil.empty()).build(), (origin, newOne) -> {
                    log.info("\r=================");
                    log.info("\r更新日期 {}", newOne.getDate());
                    log.info("\r当前持仓 ");
                    log.info("\r{} ", newOne.getItems().stream().map(
                            i -> StrUtil.format("{}({}) ", i.getName(), i.getCode())).collect(
                            Collectors.joining(",")));
                    log.info("\r调仓操作如下： ");

                    List<String> originCodes = origin.getItems().stream().map(ApiSeasonStockData::getCode).collect(
                            Collectors.toList());

                    List<String> newOneCodes = newOne.getItems().stream().map(ApiSeasonStockData::getCode).collect(
                            Collectors.toList());

                    //持仓
                    log.info("\r[持仓] {}",
                            newOne.getItems().stream().filter(i -> (originCodes.contains(i.getCode()))).map(
                                    i -> StrUtil.format("{}({}) ", i.getName(), i.getCode())).collect(
                                    Collectors.joining(",")));
                    //清仓
                    log.info("\r[清仓] {}",
                            origin.getItems().stream().filter(i -> (!newOneCodes.contains(i.getCode()))).map(
                                    i -> StrUtil.format("{}({}) ", i.getName(), i.getCode())).collect(
                                    Collectors.joining(",")));

                    //建仓
                    log.info("\r[建仓] {}",
                            newOne.getItems().stream().filter(i -> (!originCodes.contains(i.getCode()))).map(
                                    i -> StrUtil.format("{}({}) ", i.getName(), i.getCode())).collect(
                                    Collectors.joining(",")));

                    log.info("\r");
                    return newOne;
                });


        return "everything is ok";
    }

}

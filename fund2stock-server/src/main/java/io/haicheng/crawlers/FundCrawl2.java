package io.haicheng.crawlers;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基金爬虫
 *
 * @author haicheng
 */
@Slf4j
@Crawler(name = "fundCrawl2")
public class FundCrawl2 extends BaseSeimiCrawler {

    private String urlTemplate = "https://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=jjcc&code=260112&topline=10&year={}&month=&rt={}";

    @Override
    public String[] startUrls() {
        return new String[]{getUrl("2023")};
    }

    private String getUrl(String year) {
        return StrUtil.format(urlTemplate, year, (new Date()).getTime());
    }

    @Override
    public void start(Response response) {
        try {

            ApiData apiData = parseApiData(response.getContent());

            apiData.getListYear().forEach(y -> {
                push(Request.build(getUrl(String.valueOf(y)), FundCrawl2::getDetail));
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDetail(Response response) {
        try {

            log.info("开始解析地址： {}", response.getUrl());

            ApiData apiData = parseApiData(response.getContent());
            JXDocument document = JXDocument.create(apiData.getContent());

            List<JXNode> boxes = document.selN("//body/div[@class='box']");

            log.info("本年季度数据： {}", boxes.size());
            List<ApiSeasonData> apiSeasonData = CollUtil.newArrayList();

            boxes.stream().forEach(b -> {
                JXNode timeEl = b.selOne("//div/h4/label[@class='right lab2 xq505']/font[@class='px12']/text()");

                ApiSeasonData data = ApiSeasonData.builder().date(timeEl.asString()).build();


                List<JXNode> trs = b.sel("//table/tbody/tr");

                data.setItems(trs.stream().map(t -> {

                    List<JXNode> tds = t.sel("//td/text()|//td/a/text()");
                    return ApiSeasonStockData.builder()
                            .id(tds.get(0).asString())
                            .code(tds.get(4).asString())
                            .name(tds.get(5).asString())
                            .rate(tds.get(1).asString())
                            .hold(tds.get(2).asString())
                            .amount(tds.get(3).asString())
                            .build();
                }).collect(Collectors.toList()));

                apiSeasonData.add(data);
            });


            log.info("解析数据： {}", apiSeasonData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ApiData parseApiData(String response) {
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ApiData {
        String content;
        List<Integer> listYear;
        String curYear;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ApiSeasonData {
        String date;
        List<ApiSeasonStockData> items;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ApiSeasonStockData {
        String id;
        String code;
        String name;
        String rate;
        String hold;
        String amount;
    }
}

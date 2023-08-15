package io.haicheng.crawlers;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import io.haicheng.dto.ApiData;
import io.haicheng.dto.ApiSeasonData;
import io.haicheng.dto.ApiSeasonStockData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
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

    public static String path = "/Users/haicheng/code/java/fund2stok-model/logs/api-data.txt";

    @Override
    public String[] startUrls() {
        return new String[]{getUrl("2023")};
    }

    private String getUrl(String year) {
        return StrUtil.format(urlTemplate, year, (new Date()).getTime() + '.' + RandomUtil.randomInt(10000, 99999));
    }

    @Override
    public void start(Response response) {
        try {

            ApiData apiData = ApiData.parse(response.getContent());

            FileUtil.del(path);

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

            ApiData apiData = ApiData.parse(response.getContent());
            JXDocument document = JXDocument.create(apiData.getContent());

            List<JXNode> boxes = document.selN("//body/div[@class='box']");

            log.info("本年季度数据： {}", boxes.size());
            List<ApiSeasonData> apiSeasonData = CollUtil.newArrayList();

            boxes.forEach(b -> {
                JXNode timeEl = b.selOne("//div/h4/label[@class='right lab2 xq505']/font[@class='px12']/text()");

                ApiSeasonData data = ApiSeasonData.builder().date(timeEl.asString()).build();


                List<JXNode> trs = b.sel("//table/tbody/tr[*]");

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
                FileUtil.appendUtf8String(JSONUtil.toJsonStr(data) + "\r\n", path);
            });


            log.info("解析数据： {}", apiSeasonData);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

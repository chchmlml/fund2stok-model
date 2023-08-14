package io.haicheng.crawlers;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import org.jsoup.nodes.Element;
import org.seimicrawler.xpath.JXDocument;

import java.io.File;
import java.util.List;

/**
 * 基金爬虫
 *
 * @author haicheng
 */
@Crawler(name = "fundCrawl")
public class FundCrawl extends BaseSeimiCrawler {
    @Override
    public String[] startUrls() {
        //两个是测试去重的
        return new String[]{"https://www.dytt8.com/"};
    }

    @Override
    public void start(Response response) {
        JXDocument doc = response.document();
        try {
            List<Object> urls = doc.sel("//div[@class='co_area2']/div[@class='co_content8']/ul/table/tbody/tr/td/a/@href");
            logger.info("[fundCrawl] 找到链接{}", urls.size());
            for (Object s : urls) {
                push(Request.build( "https://www.dytt8.com/" + s.toString(), FundCrawl::getTitle));
//                push(Request.build(s.toString(),"getTitle"));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTitle(Response response) {
        JXDocument doc = response.document();
        try {

            Element title = (Element) doc.selOne("//div[@class='title_all']/h1/font/text()");
            logger.info("url:{} {}", response.getUrl(), title.text());

            //image.forEach(imageUrl -> {
            //    push(Request.build(String.valueOf(imageUrl), FundCrawl::downLoad));
            //});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //public void downLoad(Response response) {
    //
    //    logger.info("url:{}", response.getRealUrl());
    //    String fileName = response.getRealUrl().substring(response.getRealUrl().lastIndexOf("/") + 1);
    //    File targetFile = new File("/Users/haicheng/Downloads/wallpaper/" + fileName);
    //    response.saveTo(targetFile);
    //}
}

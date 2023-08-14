package io.haicheng.crawlers;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import org.seimicrawler.xpath.JXDocument;

import java.util.List;

@Crawler(name = "basic_a")
public class Basic extends BaseSeimiCrawler {
    @Override
    public String[] startUrls() {
        //两个是测试去重的
        return new String[]{"https://www.dytt8.com/"};
    }

    @Override
    public void start(Response response) {
        JXDocument doc = response.document();
        try {
            List<Object> urls = doc.sel("//*[@id=\"header\"]/div/div[3]/div[2]/div[2]/div[1]/div/div[2]/div[2]/ul/table/tbody/tr[5]/td[1]");
            logger.info("[basic_a]找到链接{}", urls.size());
            for (Object s : urls) {
                push(Request.build(s.toString(), Basic::getTitle));
//                push(Request.build(s.toString(),"getTitle"));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTitle(Response response) {
        JXDocument doc = response.document();
        try {
            logger.info("[basic_a] g获取url:{} {}", response.getUrl(),
                    doc.sel("//a/text()"));
            //do something
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

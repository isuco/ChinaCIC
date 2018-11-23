package cn.edu.bupt.chinacic.service;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ConfigService {

    @Value("numToNameXmlPath")
    private String numToNameXmlPath;

    public static Map<Character, String> types;

    public static List<String> voteItems = new ArrayList<>();

    @PostConstruct
    public void init() {
        File file = new File(numToNameXmlPath);
        if (!file.exists() || file.isDirectory()) {
            log.error("奖项编号配置文件{}不存在或是文件夹", numToNameXmlPath);
            return;
        }
        types = new HashMap<>();
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(file);
            Element rootElement = document.getRootElement();
            for (Element element : rootElement.elements()) {
                String num = element.element("num").getText();
                String name = element.element("name").getText();
                if (num != null && num.length() > 0) {
                    types.put(num.charAt(0), name);
                }
            }
        } catch (DocumentException e) {
            log.error("文件{}解析失败", numToNameXmlPath);
            e.printStackTrace();
        }
    }

}

package cn.edu.bupt.chinacic.controller;

import cn.edu.bupt.chinacic.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private IndexService indexService;

    @Autowired
    public void setIndexService(IndexService indexService) {
        this.indexService = indexService;
    }

    @GetMapping("/")
    public String index(ModelMap resMap) {
        resMap.put("projects", indexService.getAllProjects());
        return "index";
    }

}

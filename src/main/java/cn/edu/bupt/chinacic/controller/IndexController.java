package cn.edu.bupt.chinacic.controller;

import cn.edu.bupt.chinacic.service.IndexService;
import cn.edu.bupt.chinacic.util.NetworkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    private IndexService indexService;

    @Autowired
    public void setIndexService(IndexService indexService) {
        this.indexService = indexService;
    }

    @GetMapping("/")
    public String index(HttpServletRequest request, ModelMap resMap) {
        String ip = NetworkUtils.getIpAddr(request);
        if (indexService.needRegistry(ip)) {
            return "registry";
        }else {
            return "index";
        }
    }

}

package cn.edu.bupt.chinacic.controller;

import cn.edu.bupt.chinacic.pojo.vo.HomeTreeVo;
import cn.edu.bupt.chinacic.pojo.vo.VoteItemVo;
import cn.edu.bupt.chinacic.service.ConfigService;
import cn.edu.bupt.chinacic.service.IndexService;
import cn.edu.bupt.chinacic.service.UserService;
import cn.edu.bupt.chinacic.util.CommonResult;
import cn.edu.bupt.chinacic.util.NetworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sun.security.krb5.Config;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("user")
@Slf4j
public class UserController {

    private UserService userService;
    private IndexService indexService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setIndexService(IndexService indexService) {
        this.indexService = indexService;
    }

    @GetMapping("registry")
    public String userRegistryView() {
        return "registry";
    }

    @PostMapping("registry")
    public String userRegistry(HttpServletRequest request, @RequestParam String name, ModelMap resMap) {
        String ip = NetworkUtils.getIpAddr(request);
        if (StringUtils.isEmpty(ip) || StringUtils.isEmpty(name)) {
            resMap.put("error", "注册信息不完整");
            return "registry";
        }
        log.info("注册用户 name = {}, ip = {}", name, ip);
        boolean isSuccess = userService.registryUser(ip, name);
        if (!isSuccess) {
            log.error("您的Ip = {}已注册", ip, name);
            resMap.put("error", "用户已存在，不能重复注册");
            return "registry";
        } else {
            log.info("用户 ip = {}, name = {}注册成功", ip, name);
            return "index";
        }
    }

    @GetMapping("projects")
    @ResponseBody
    public CommonResult getAllProjects() {
        List<HomeTreeVo> projects = userService.getAllPublishedProjects();
        if (projects == null || projects.size() == 0) {
            return CommonResult.failure("没有发布的参评项目");
        } else {
            return CommonResult.success("success", projects);
        }
    }

    @GetMapping("vote")
    public String getVoteView(HttpServletRequest request, ModelMap resMap) {
        String ip = NetworkUtils.getIpAddr(request);
        if (!StringUtils.isEmpty(ip) || !indexService.needRegistry(ip)) {
            List<VoteItemVo> vos = userService.getVoteData(ip);
            if (vos.size() == 0) {
                resMap.addAttribute("error", "没有参与投票的项目");
            } else {
                resMap.addAttribute("voteData", vos);
                resMap.addAttribute("prizes", ConfigService.voteItems);
            }
        } else {
            resMap.addAttribute("error", "没有投票资格");
        }
        return "expert-vote";
    }

}

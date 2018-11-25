package cn.edu.bupt.chinacic.controller;

import cn.edu.bupt.chinacic.pojo.po.Project;
import cn.edu.bupt.chinacic.pojo.vo.HomeTreeVo;
import cn.edu.bupt.chinacic.service.UserService;
import cn.edu.bupt.chinacic.util.CommonResult;
import cn.edu.bupt.chinacic.util.NetworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("user")
@Slf4j
public class UserController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
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
    public CommonResult getAllProjects(){
        List<HomeTreeVo> projects = userService.getAllPublishedProjects();
        if(projects==null||projects.size()==0){
            return CommonResult.failure("没有发布的参评项目");
        }else{
            return CommonResult.success("success", projects);
        }
    }

}

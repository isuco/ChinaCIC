package cn.edu.bupt.chinacic.controller;

import cn.edu.bupt.chinacic.service.UserService;
import cn.edu.bupt.chinacic.util.CommonResult;
import cn.edu.bupt.chinacic.util.NetworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("user")
@Slf4j
public class UserController {

    private UserService userService;

    @Value("adminPassword")
    private String adminPassword;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("registry")
    @ResponseBody
    public CommonResult userRegistry(HttpServletRequest request, @RequestParam String name) {
        String ip = NetworkUtils.getIpAddr(request);
        if (StringUtils.isEmpty(ip) || StringUtils.isEmpty(name)) {
            return CommonResult.failure("注册信息不完整");
        }
        log.info("注册用户 name = {}, ip = {}", name, ip);
        boolean isSuccess = userService.registryUser(ip, name);
        if (!isSuccess) {
            log.error("您的Ip = {}已注册", ip, name);
            return CommonResult.failure("用户已存在，不能重复注册");
        } else {
            log.info("用户 ip = {}, name = {}注册成功", ip, name);
            return CommonResult.success("注册成功");
        }
    }

    @PostMapping("login")
    public String adminLogin(@RequestParam String password, ModelMap resMap) {
        if (adminPassword.equals(password)) {
            resMap.put("projects", this.userService.getAllProject());
            return "operation";
        } else {
            return "redirect:/";
        }
    }

}

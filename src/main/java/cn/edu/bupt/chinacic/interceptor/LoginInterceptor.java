package cn.edu.bupt.chinacic.interceptor;

import cn.edu.bupt.chinacic.service.IndexService;
import cn.edu.bupt.chinacic.util.NetworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private IndexService indexService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI();
        if (url.startsWith("/css") || url.startsWith("/js") || url.startsWith("/img") || url.startsWith("font"))
            return true;
        if (!url.equals("/user/registry") && !url.equals("/admin/login")) {
            if (url.startsWith("/user")) {
                if (indexService.needRegistry(NetworkUtils.getIpAddr(request)))
                    response.sendRedirect("/user/registry");
            }
            if (url.startsWith("/admin")) {
                Boolean adminLogin = Boolean.valueOf(String.valueOf(request.getSession().getAttribute("adminLogin")));
                if (adminLogin) {
                    return true;
                } else {
                    response.sendRedirect("/admin/login");
                }
            }
        }
        return true;
    }
}

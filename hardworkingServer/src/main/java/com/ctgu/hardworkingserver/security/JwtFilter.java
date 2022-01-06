package com.ctgu.hardworkingserver.security;

import com.ctgu.hardworkingserver.utils.FeedBack;
import com.ctgu.hardworkingserver.utils.JSONTransferUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 过滤器，避免重复无效使用shiro鉴权
 * @author BeastHoo
 * Date 2021/11/9
 */
@Slf4j
@Component
public class JwtFilter extends BasicHttpAuthenticationFilter {
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        JSONTransferUtil.setResp(httpServletResponse);
        String token = ((HttpServletRequest) request).getHeader("authorization");
        FeedBack feedBack = new FeedBack();


        //无token说明未登录，拒绝请求
        if(token == null || "".equals(token)){
            feedBack.setStatus(0);
            feedBack.setMessage("未登录,无权访问,请先登录");
            String json = JSONTransferUtil.transferBeanToJson(feedBack);
            try {
                httpServletResponse.getWriter().write(json);
            } catch (IOException e) {
                e.printStackTrace();
                log.info("向前端发送数据失败");
            }
            return false;
        }

        //token存在，进行验证

        JwtToken jwtToken = new JwtToken(token);
        try {
            SecurityUtils.getSubject().login(jwtToken);  //通过subject，提交给myRealm进行登录验证
            return true;
        } catch (ExpiredCredentialsException e){
            //token过期
            feedBack.setStatus(0);
            feedBack.setMessage("登录过期, 请重新登录!");
            String s = JSONTransferUtil.transferBeanToJson(feedBack);
            httpServletResponse.getWriter().write(s);
            e.printStackTrace();
            return false;
        } catch (ShiroException e){
            // 其他情况抛出的异常统一处理，由于先前是登录进去的了，所以都可以看成是token被伪造造成的
            feedBack.setStatus(2);
            feedBack.setMessage("登录状态出错!请重新登录");
            String s = JSONTransferUtil.transferBeanToJson(feedBack);
            httpServletResponse.getWriter().write(s);
            e.printStackTrace();
            return false;
        }
    }


    //登录成功
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        return super.onLoginSuccess(token, subject, request, response);
    }

    //过滤器拦截请求的入口方法
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        try {
            return executeLogin(request, response);  //token验证
        } catch (Exception e) {
            e.printStackTrace();
            log.info("过滤器出错");
            return false;
        }
    }


    //isAccessAllowed()方法返回false，即认证不通过时进入onAccessDenied方法
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return super.onAccessDenied(request, response);
    }
}

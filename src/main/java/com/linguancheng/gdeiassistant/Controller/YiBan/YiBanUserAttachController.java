package com.linguancheng.gdeiassistant.Controller.YiBan;

import com.linguancheng.gdeiassistant.Enum.Base.LoginResultEnum;
import com.linguancheng.gdeiassistant.Exception.CommonException.TransactionException;
import com.linguancheng.gdeiassistant.Pojo.Entity.User;
import com.linguancheng.gdeiassistant.Pojo.Redirect.RedirectInfo;
import com.linguancheng.gdeiassistant.Pojo.Result.JsonResult;
import com.linguancheng.gdeiassistant.Pojo.Result.BaseResult;
import com.linguancheng.gdeiassistant.Pojo.UserLogin.UserCertificate;
import com.linguancheng.gdeiassistant.Service.UserData.UserDataService;
import com.linguancheng.gdeiassistant.Service.UserLogin.UserLoginService;
import com.linguancheng.gdeiassistant.Service.YiBan.YiBanUserDataService;
import com.linguancheng.gdeiassistant.Tools.HttpClientUtils;
import com.linguancheng.gdeiassistant.Tools.StringUtils;
import com.linguancheng.gdeiassistant.ValidGroup.User.UserLoginValidGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class YiBanUserAttachController {

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private YiBanUserDataService yiBanUserDataService;

    @RequestMapping("/yiban/attach")
    public ModelAndView ResolveYiBanUserAttachPage(HttpServletRequest request, RedirectInfo redirectInfo) {
        ModelAndView modelAndView = new ModelAndView();
        String userid = (String) request.getSession().getAttribute("yiBanUserID");
        if (userid == null || userid.trim().isEmpty()) {
            modelAndView.addObject("ErrorMessage", "用户授权已过期，请重新登录并授权");
            modelAndView.setViewName("YiBan/yibanError");
            return modelAndView;
        }
        if (redirectInfo.needToRedirect()) {
            modelAndView.addObject("RedirectURL", redirectInfo.getRedirect_url());
        }
        modelAndView.setViewName("YiBan/yibanAttach");
        return modelAndView;
    }

    @RequestMapping("/yiban/userattach")
    @ResponseBody
    public JsonResult YiBanUserAttach(HttpServletRequest request, @Validated(value = UserLoginValidGroup.class) User user) throws Exception {
        JsonResult result = new JsonResult();
        String yiBanUserID = (String) request.getSession().getAttribute("yiBanUserID");
        if (StringUtils.isBlank(yiBanUserID)) {
            return new JsonResult(false, "用户授权已过期，请重新登录并授权");
        }
        //清除已登录用户的用户凭证记录
        HttpClientUtils.ClearHttpClientCookieStore(request.getSession().getId());
        UserCertificate userCertificate = userLoginService
                .UserLogin(request.getSession().getId(), user, true);
        //同步用户教务系统账号信息到数据库
        User resultUser = userCertificate.getUser();
        //同步用户数据
        userDataService.SyncUserData(resultUser);
        //同步易班数据
        yiBanUserDataService.SyncYiBanUserData(resultUser.getUsername(), yiBanUserID);
        //将用户信息数据写入Session
        request.getSession().setAttribute("username", resultUser.getUsername());
        request.getSession().setAttribute("password", resultUser.getPassword());
        //异步同步教务系统会话
        userLoginService.AsyncUpdateSession(request);
        result.setSuccess(true);
        return result;
    }
}

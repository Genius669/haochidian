package com.zhao.haochidian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhao.haochidian.common.R;
import com.zhao.haochidian.entity.User;
import com.zhao.haochidian.service.UserService;
import com.zhao.haochidian.utils.ValidateCodeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            //生成4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            System.out.println(code);
            /**
             * 调用API发送短信
             * SMSUtils.sendMessage("sign","",phone,code);
             */
            //验证码保存才session
            //session.setAttribute(phone, code);

            //将生成的验证码存入redis，保存时间5min
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return R.success("验证码发送成功");
        }
        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //验证码比对
        //Object codeInSession = session.getAttribute(phone);
        //从redis中取出验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        if (codeInSession != null && codeInSession.equals(code)) {
            //对比成功登录成功

            //判断是否为新用户，新用户自动注册
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, phone);
            User user = userService.getOne(wrapper);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            //登录成功，删除redis中的数据
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登录失败");
    }


}

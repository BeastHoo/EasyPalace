package com.ctgu.hardworkingserver.controller;


import com.ctgu.hardworkingserver.components.MailService;
import com.ctgu.hardworkingserver.entity.User;
import com.ctgu.hardworkingserver.security.JwtToken;
import com.ctgu.hardworkingserver.service.UserServiceImp;
import com.ctgu.hardworkingserver.utils.Constant;
import com.ctgu.hardworkingserver.utils.FeedBack;
import com.ctgu.hardworkingserver.utils.JSONTransferUtil;
import com.ctgu.hardworkingserver.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSON;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * 提供用户注册，登录相关操作
 * @author BeastHoo
 * Date 2021/11/8
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MailService mailService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserServiceImp userService;

    //注册：向用户邮箱发送验证码
    @RequestMapping("/sendverifycode")
    public void sendEmail(@RequestParam(name = "email") String email,
                          @RequestParam(name = "name") String name,
                          @RequestParam(name = "password") String password,
                          HttpServletResponse response){
        User user =new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setUsername(name);

        //通过uuid获取六位随机验证码
        String uuId = UUID.randomUUID().toString();
        String code = uuId.substring(uuId.length()-6);

        //邮件内容
        String content = "<div style=\"background:linear-gradient(120deg, #3498db, #8e44ad)\">" +
                "<h2 style=\"color: white\">" +name+
                ":</h2><br/>"+ "<p>&nbsp;&nbsp;&nbsp;&nbsp;您的验证码为："+"<i style=\"color:red\">"+code+"</i>(有效期为五分钟)</p>"+
                "</div>";
        ValueOperations<String, User> operations= redisTemplate.opsForValue();
        //将验证码及用户信息存放进redis中,过期时间为五分钟
        operations.set(code, user, 5, TimeUnit.MINUTES);
        mailService.sendMail("1369843192@qq.com",email,null,"EasyPalace验证",content);
    }


    //执行注册
    @RequestMapping("/regist")
    public void register(@RequestParam("encryptedCode")String code, HttpServletResponse response)
    {
        FeedBack feedBack = new FeedBack();
        //从redis中获取验证码，成功则注册，失败则返回失败原因及代码
        User user = (User)redisTemplate.opsForValue().get(code);
        if (user==null){
            feedBack.setStatus(0);
            feedBack.setMessage("注册失败!验证码错误或已过期!");
        }
        else{
            int rec = userService.register(user);
            if (rec == 0)
            {
                feedBack.setStatus(0);
                feedBack.setMessage("注册失败!请重新注册!");
            }else{
                feedBack.setStatus(1);
                feedBack.setMessage("注册成功!请登录当前账号并完善个人信息~");
            }
        }
        String result = JSONTransferUtil.transferBeanToJson(feedBack);
        JSONTransferUtil.setResp(response);
        try {
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //注册：查找用户名是否已经存在
    //忘记密码：验证用户名是否正确
    @RequestMapping("/nameRepeat")
    public void find(HttpServletResponse response,@RequestParam(name = "username")String username){
        log.info("查询用户名是否重复: "+username);
        FeedBack feedBack = new FeedBack();
        if (userService.findout(username) == 0)
        {
            feedBack.setStatus(1);
            feedBack.setMessage("该用户名可以使用!");
        }else{
            feedBack.setStatus(0);
            feedBack.setMessage("该用户名不可以使用");
        }
        String result = JSONTransferUtil.transferBeanToJson(feedBack);
        JSONTransferUtil.setResp(response);
        try {
//            log.info(result);
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //注册：查找邮箱号码是否已经被绑定
    @RequestMapping("/checkEmail")
    public void checkEmail(HttpServletResponse response,@RequestParam(name = "email") String email)
    {
        log.info("查询邮箱是否已经被绑定:"+email);
        JSONTransferUtil.setResp(response);
        FeedBack feedBack = new FeedBack();
        int email1 = userService.findEmail(email);
        if (userService.findEmail(email) == 0)
        {
            feedBack.setStatus(0);
            feedBack.setMessage("该邮箱已经被注册");
        }else {
            feedBack.setStatus(1);
            feedBack.setMessage("该邮箱可以使用");
        }

        String result = JSONTransferUtil.transferBeanToJson(feedBack);
        System.out.println(result);
        JSONTransferUtil.setResp(response);
        try {
//            log.info(result);
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //登录
    @RequestMapping("/login")
    public void login(@RequestParam(name = "username") String username,
                      @RequestParam(name = "pwd") String password,
                      HttpServletResponse response){
        //根据用户名获取正确用户信息
        User user = userService.selectUserByName(username);
        FeedBack feedBack = new FeedBack();
        JSONTransferUtil.setResp(response);


        if(user == null)
        {
            feedBack.setStatus(0);
            feedBack.setMessage("无该账户的信息!");
            String s = JSONTransferUtil.transferBeanToJson(feedBack);
            try {
                response.getWriter().write(s);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        Map<String,String> map = new HashMap<>();
        map.put("username",username);
        Md5Hash md5Hash = new Md5Hash(password,user.getSalt(),1024);
        map.put("password",md5Hash.toHex());

        //生成token字符串
        String token = JwtUtil.getToken(map);
        //转换成jwtToken
        JwtToken jwtToken = new JwtToken(token);
        log.info("jwtToken:"+jwtToken.getCredentials());
        //拿到Subject对象
        Subject subject = SecurityUtils.getSubject();

        //进行认证
        try {
            //登录成功
            subject.login(jwtToken);
            feedBack.setStatus(1);
            feedBack.setMessage(username);
            response.setHeader("authorization",token);
            log.info("登录成功");
        } catch (UnknownAccountException e){
            //用户不存在
            feedBack.setStatus(0);
            feedBack.setMessage("账户不存在!");
            e.printStackTrace();
        } catch (IncorrectCredentialsException e){
            //密码输入出差错
            feedBack.setStatus(0);
            feedBack.setMessage("密码错误!");
            e.printStackTrace();
        } catch (ExpiredCredentialsException e){
            //token过期
            feedBack.setStatus(0);
            feedBack.setMessage("token已经过期");
            e.printStackTrace();
        }
        String s = JSONTransferUtil.transferBeanToJson(feedBack);
        try {
            response.getWriter().write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //忘记密码:验证邮箱地址与账户是否一致
    @RequestMapping("/findEmail")
    public void FindEmail(@RequestParam(name = "email") String email,
                          @RequestParam(name = "username") String username,
                          HttpServletResponse response)
    {
        ValueOperations<String, User> opt = redisTemplate.opsForValue();
        User user = opt.get(username);
        ValueOperations<String,String> valueOperations =redisTemplate.opsForValue();
        FeedBack feedBack = new FeedBack();
        JSONTransferUtil.setResp(response);

        //若redis中有此前查找的用户信息，且邮箱匹配
        if (user != null)
        {
            if (user.getEmail().equals(email))
            {
                feedBack.setStatus(1);
                feedBack.setMessage("邮箱验证成功!");

                //通过uuid获取六位随机验证码
                String uuId = UUID.randomUUID().toString();
                String code = uuId.substring(uuId.length()-6);

                String content = "<div style=\"background:linear-gradient(120deg, #3498db, #8e44ad)\">" +
                        "<h2 style=\"color: white\">" +username+
                        ":</h2>"+ "<p>&nbsp;&nbsp;&nbsp;&nbsp;！您正在修改您的账户密码，如非本人操作，请忽略此邮件！您的验证码为："+"<i style=\"color:red\">"+code+"</i>(有效期为五分钟，请勿泄漏给他人)</p>"+
                        "</div>";

                mailService.sendMail("1369843192@qq.com",email,null,"EasyPalace验证",content);
                valueOperations.set(email,code,5,TimeUnit.MINUTES);
            }
            else
            {
                feedBack.setStatus(0);
                feedBack.setMessage("邮箱不匹配，请重新输入!");
            }
            String s = JSONTransferUtil.transferBeanToJson(feedBack);
            try {
                response.getWriter().write(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            feedBack.setStatus(2);
            feedBack.setMessage("服务超时,请重试");
            String s = JSONTransferUtil.transferBeanToJson(feedBack);
            try {
                response.getWriter().write(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //验证码信息
    @RequestMapping("/verCode")
    public void verCode(@RequestParam(name = "email")String email,
                        @RequestParam(name = "code") String curCode,
                        HttpServletResponse response){
        ValueOperations<String,String> opt = redisTemplate.opsForValue();
        String preCode = opt.get(email);
        JSONTransferUtil.setResp(response);
        FeedBack feedBack = new FeedBack();
        if (preCode != null)
        {
            if (preCode.equals(curCode))
            {
                feedBack.setStatus(1);
                feedBack.setMessage("验证通过");
            }
            else{
                feedBack.setStatus(0);
                feedBack.setMessage("验证码不匹配!");
            }
            String s = JSONTransferUtil.transferBeanToJson(feedBack);
            try {
                response.getWriter().write(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            feedBack.setStatus(0);
            feedBack.setMessage("验证码已失效!");
            String s = JSONTransferUtil.transferBeanToJson(feedBack);
            try {
                response.getWriter().write(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //重设密码
    @RequestMapping("/reset")
    public void resetPwd(@RequestParam(name = "password")String password,
                         @RequestParam(name = "name")String username,
                         HttpServletResponse response){
        ValueOperations<String ,User> opt = redisTemplate.opsForValue();
        User user = opt.get(username);
        FeedBack feedBack = new FeedBack();
        if (user != null)
        {
            String uuId = UUID.randomUUID().toString();
            String salt = uuId.substring(uuId.length()-6);
            Md5Hash md5Hash = new Md5Hash(password,salt,1024);
            user.setPassword(md5Hash.toHex());
            user.setSalt(salt);
            int temp = userService.update(user);
            if (temp != 0)
            {
                feedBack.setStatus(1);
                feedBack.setMessage("密码重设成功!");
                opt.set(user.getUsername(),user,30,TimeUnit.MINUTES);
            }
            else
            {
                feedBack.setStatus(0);
                feedBack.setMessage("密码重设失败!请重试...");
            }
        }else{
            feedBack.setStatus(2);
            feedBack.setMessage("服务超时,请重试!");
        }
        JSONTransferUtil.setResp(response);
        try {
            response.getWriter().write(JSONTransferUtil.transferBeanToJson(feedBack));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     *取消图片直接上传到服务器，改为上传到oss
     */
//    @RequestMapping("/avatar")
//    public void handleUploadAvatar(HttpServletRequest request,
//                                   HttpServletResponse response){
//        MultipartFile file = ((MultipartHttpServletRequest) request).getFile("file");
//
//        log.info("-----Avatar resource is in-----");
//        assert file != null;
//        String oldName =  file.getOriginalFilename();
//        assert oldName != null;
//        String suffixName = oldName.substring(oldName.lastIndexOf("."));
//        String newName = UUID.randomUUID().toString() + suffixName;
//        BufferedOutputStream stream = null;
//        String username = JwtUtil.getTokenInfo(request.getHeader("authorization")).getClaim("username").asString();
//
//        FeedBack feedBack = new FeedBack();
//        JSONTransferUtil.setResp(response);
//        String filePath = "./FILE/" + username + "/";            // 文件路径
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//        log.info("上传头像: "+filePath);
//        File dest = new File(filePath);
//        // 检测是否存在目录
//        if (!dest.exists()) {
//           boolean flag= dest.mkdirs();// 新建文件夹
//            log.info("创建文件夹"+ filePath+"  "+flag );
//        }
//        else {
//            log.info(dest.getAbsolutePath());
//        }
//
//        byte[] bytes = new byte[0];
//        try {
//            bytes = file.getBytes();
//            stream = new BufferedOutputStream(new FileOutputStream(
//                    filePath + newName));//设置文件路径及名字
//            stream.write(bytes);// 写入
//            stream.close();
////            file.transferTo(new File(filePath));
////            username = URLEncoder.encode(username);
//            String imgUrl =
//                    request.getScheme() + "://" + request.getServerName() + ":" +request.getServerPort() +
//                            "/blog/getImg/"+"username=" +username +"&type=avatar"+"&filename="+ newName;
//            log.info(imgUrl);
//            User user = userService.selectUserByName(username);
//            user.setImageUrl(imgUrl);
//            userService.update(user);
//            feedBack.setStatus(1);
//            feedBack.setMessage(imgUrl);
//        } catch (Exception e) {
//            e.printStackTrace();
//            feedBack.setStatus(0);
//            feedBack.setMessage("上传失败");
//        }
//
//        try {
//            String s = JSONTransferUtil.transferBeanToJson(feedBack);
//            response.getWriter().write(s);
//            log.info("-----Finish Uploading Avatar-----");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }



    //获取用户信息
    @RequestMapping("/finduserinfo")
    public void getUserInfo(HttpServletRequest request,HttpServletResponse response){
        String username = JwtUtil.getTokenInfo(request.getHeader("authorization")).getClaim("username").asString();
        User user = userService.selectUserByName(username);

        user.setPassword(null);
        user.setSalt(null);

        JSONTransferUtil.setResp(response);
        try {
            response.getWriter().write(JSONTransferUtil.transferBeanToJson(user));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    @RequestMapping("/updateUser")
    public void updateUser(HttpServletResponse response,
                           @RequestParam(name = "username")String username,
                           @RequestParam(name = "slogan")String slogan,
                           @RequestParam(name = "signature")String signature,
                           @RequestParam(name = "gender")String gender,
                           @RequestParam(name = "birthday")String birthday,
                           @RequestParam(name = "imageUrl")String url) throws  Exception{

        User user = new User();
        user.setUsername(username);
        user.setBirthday(birthday);
        user.setGender(gender);
        user.setSignature(signature);
        user.setSlogan(slogan);
        user.setImageUrl(url);

        log.info(user.toString());

        int cnt = userService.updateUserInfo(user);

        ValueOperations<String, User> valueOperations = redisTemplate.opsForValue();
        valueOperations.getOperations().delete(username);

        FeedBack feedBack = new FeedBack();
        JSONTransferUtil.setResp(response);
        if (cnt == 0)
        {
            feedBack.setStatus(0);
            feedBack.setMessage("更新失败");
            response.getWriter().write(JSONTransferUtil.transferBeanToJson(feedBack));
            throw new Exception("更新用户信息出错");
        }
        else
        {
            feedBack.setStatus(1);
            feedBack.setMessage("更新成功");
            response.getWriter().write(JSONTransferUtil.transferBeanToJson(feedBack));
        }
    }

    @RequestMapping("/findEditor")
    public void findEditor(HttpServletResponse response,
                           @RequestParam(name = "username") String username){
        User user = userService.selectUserByName(username);

        user.setPassword(null);
        user.setSalt(null);
        user.setRegisttime(null);
        JSONTransferUtil.setResp(response);
        try {
            response.getWriter().write(JSONTransferUtil.transferBeanToJson(user));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

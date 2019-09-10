package edu.uta.cse.microservice.user.controller;

import edu.uta.cse.microservice.user.redis.RedisClient;
import edu.uta.cse.microservice.user.response.LoginResponse;
import edu.uta.cse.microservice.user.response.Response;
import edu.uta.cse.microservice.user.thrift.ServiceProvider;
import edu.uta.cse.thrift.user.UserInfo;
import edu.uta.cse.thrift.user.dto.UserDTO;
import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Exposed user service interface, example:
 * http://localhost:8082/user/login?...
 */

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private ServiceProvider serviceProvider;

    @Autowired
    private RedisClient redisClient;

    /* A simple thymeleaf webpage for login as demo purpose */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Response login(@RequestParam("username") String username,
                          @RequestParam("password") String password) {

        // 1. Verify username & password
        UserInfo userInfo;
        try {
            userInfo = serviceProvider.getUserService().getUserByName(username);
        } catch (TException e) {
            e.printStackTrace();
            return Response.USERNAME_PASSWORD_INVALID;
        }
        if (userInfo == null) {
            return Response.USERNAME_PASSWORD_INVALID;
        }
        //DB saves password in md5
        if (!userInfo.getPassword().equalsIgnoreCase(md5(password))) {
            return Response.USERNAME_PASSWORD_INVALID;
        }
        // 2. gen token
        String token = getToken();

        // 3. Cache user info (stateless) and return
        redisClient.set(token, toDTO(userInfo), 3600);
        return new LoginResponse(token);
    }

    @RequestMapping(value = "/sendVerification", method = RequestMethod.POST)
    @ResponseBody
    public Response sendVerifyCode(@RequestParam(value = "mobile_number", required = false) String mobile,
                                   @RequestParam(value = "email", required = false) String email) {

        final String message = "Verify code is:";
        final String code = randomCode("0123456789", 6); //generate 6-digit verify code
        try {
            boolean result;
            if (!StringUtils.isBlank(mobile)) {
                result = serviceProvider.geMessageService().sendMobileMSG(mobile, message + code);
                redisClient.set(mobile, code);//cache the verification code to verify user's input usage
            } else if (!StringUtils.isBlank(email)) {
                result = serviceProvider.geMessageService().sendEmailMessage(email, message + code);
                redisClient.set(email, code);
            } else {
                return Response.MOBILE_OR_EMAIL_REQUIRED;
            }

            if (!result) {
                return Response.SEND_VERIFY_CODE_FAILED;
            }
        } catch (TException e) {
            e.printStackTrace();
            return Response.exception(e);
        }

        return Response.SUCCESS;
    }

    /**
     * Register a username with mobile number or email addr
     * @param username
     * @param password
     * @param mobile
     * @param email
     * @param verificationCode
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Response register(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam(value = "mobile_number", required = false) String mobile,
                             @RequestParam(value = "official_name", required = false) String officialName,
                             @RequestParam(value = "email", required = false) String email,
                             @RequestParam("verification_code") String verificationCode) {

        if (StringUtils.isBlank(mobile) && StringUtils.isBlank(email)) {
            return Response.MOBILE_OR_EMAIL_REQUIRED;
        }

        if (!StringUtils.isBlank(mobile)) {//Verify code from mobile, o.w. verify code from email
            //compare the code with cached code
            String redisCode = redisClient.get(mobile);
            if (!verificationCode.equals(redisCode)) {
                return Response.VERIFY_CODE_INVALID;
            }
        } else {
            String redisCode = redisClient.get(email);
            if (!verificationCode.equals(redisCode)) {
                return Response.VERIFY_CODE_INVALID;
            }
        }

        /* If verification success, register the user */
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setPassword(md5(password));
        userInfo.setMobile(mobile);
        userInfo.setEmail(email);
        userInfo.setOfficialName(officialName);

        try {
            serviceProvider.getUserService().registerUser(userInfo);
        } catch (TException e) {
            return Response.exception(e);
        }

        return Response.SUCCESS;
    }

    @RequestMapping(value = "/authentication", method = RequestMethod.POST)
    @ResponseBody
    public UserDTO authentication(@RequestHeader("token") String token) {
        return redisClient.get(token);
    }

    /* Thrift generated UserInfo class is not efficient for serialize, define a UserDTO class for serialize usage */
    private UserDTO toDTO(UserInfo userInfo) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userInfo, userDTO);
        return userDTO;
    }

    /**
     * Generate a 32bits token from 0~9 and a~z
     * @return
     */
    private String getToken() {
        return randomCode("0123456789abcdefghijklmnopqrstuvwxyz", 32);
    }

    private String randomCode(String s, int size) {
        StringBuilder result = new StringBuilder(size);
        Random random = new Random();
        int len = s.length();
        for (int i = 0; i < size; i++) {
            int loc = random.nextInt(len);
            result.append(s.charAt(loc));
        }
        return result.toString();
    }

    /**
     * MD5 translate
     * @param password
     * @return
     */
    private String md5(String password) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(password.getBytes());
            return HexUtils.toHexString(md5Bytes);//transfer to hex
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}

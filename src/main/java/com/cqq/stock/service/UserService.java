package com.cqq.stock.service;

import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cqq.stock.entity.TelephoneCode;
import com.cqq.stock.entity.UserInfo;
import com.cqq.stock.mapper.TelephoneCodeMapper;
import com.cqq.stock.mapper.UserInfoMapper;
import com.cqq.stock.util.QSMS;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private TelephoneCodeMapper telephoneCodeMapper;
    private UserInfoMapper userInfoMapper;

    public String login(String username, String password) {

        List<UserInfo> userInfos = userInfoMapper.selectList(
                Wrappers.<UserInfo>query().lambda()
                        .eq(UserInfo::getUsername, username)
                        .eq(UserInfo::getPassword, password)
        );
        if (userInfos.size() == 0) {
            return "error";
        }

        if (userInfos.size() != 1) {
            return "error";
        }

        UserInfo userInfo = userInfos.get(0);
        String token = UUID.randomUUID().toString();


        return null;
    }

    public String sendCode(String telephone) {

        List<TelephoneCode> telephoneCodes = telephoneCodeMapper.selectList(
                Wrappers.<TelephoneCode>query().lambda().eq(TelephoneCode::getTelephone, telephone)
        );
        if (telephoneCodes.size() == 0 || telephoneCodes.get(telephoneCodes.size() - 1).getDateTime() < (int) (System.currentTimeMillis() / 1000)) {
            Random random = new Random();
            int i = random.nextInt(1000);
            String code = String.format("%04d", i);
            System.out.println(code);
            TelephoneCode telephoneCode = new TelephoneCode();
            telephoneCode.setCode(code);
            telephoneCode.setTelephone(telephone);
            telephoneCode.setDateTime((int) (System.currentTimeMillis() / 1000) + 5 * 60);
            telephoneCodeMapper.insert(telephoneCode);
//        sendCode(telephone, code);
            return "success";
        }
        return "error";
    }

    private void sendCode(String telephone, String code) {
        try {
            QSMS.sendMessage(telephone, code);
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }


    public String register(String code, String telephone, String username, String password) {

        List<UserInfo> userInfos = userInfoMapper.selectList(Wrappers.<UserInfo>query().lambda()
                .eq(UserInfo::getUsername, username)
                .or()
                .eq(UserInfo::getTelephone, telephone)
        );
        if (userInfos.size() > 0) {
            return "already_register";
        }
        List<TelephoneCode> telephoneCodes = telephoneCodeMapper.selectList(Wrappers.<TelephoneCode>query().lambda()
                .eq(TelephoneCode::getCode, code)
                .eq(TelephoneCode::getTelephone, telephone)
                .gt(TelephoneCode::getDateTime, System.currentTimeMillis() / 1000)
        );
        if (telephoneCodes.size() > 0) {
            UserInfo userInfo = new UserInfo();
            userInfo.setUsername(username);
            userInfo.setPassword(password);
            userInfo.setTelephone(telephone);
            userInfo.setBudget("0");
            userInfo.setNickname("游客" + System.currentTimeMillis() % 123);
            userInfo.setEmail("");
            userInfoMapper.insert(userInfo);
            return "success";
        }
        return "error";
    }
}

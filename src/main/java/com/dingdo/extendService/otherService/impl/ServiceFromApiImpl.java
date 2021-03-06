package com.dingdo.extendService.otherService.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dingdo.enums.RobotAppidEnum;
import com.dingdo.enums.UrlEnum;
import com.dingdo.extendService.model.msgFromSiZhi.ChatMsg;
import com.dingdo.extendService.otherService.ServiceFromApi;
import com.dingdo.msgHandler.model.ReqMsg;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;


@Service
public class ServiceFromApiImpl implements ServiceFromApi {

    private static final Logger logger = Logger.getLogger(ServiceFromApiImpl.class);

    private final RestTemplate restTemplate;

    public ServiceFromApiImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public String sendMsgFromApi(ReqMsg reqMsg) {
        String msg = reqMsg.getRawMessage();
        JSONObject json = new JSONObject();

        // 对啥也不说的人的回答
        if (StringUtils.isBlank(msg)) {
            if (reqMsg.getMessageType().equals("private")) {
                return "你想对我说什么呢？";
            }
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        json.put("spoken", msg);//请求的文本
        json.put("appid", RobotAppidEnum.getAppidByMsg(reqMsg)); // 先使用统一的机器人pid
        json.put("userid", reqMsg.getUserId()); //自己管理的用户id，填写可进行上下文对话

        HttpEntity<JSONObject> request = new HttpEntity<>(json, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(UrlEnum.SI_ZHI_API.toString(), request, String.class);
            ChatMsg apiMsg = JSON.parseObject(response.getBody(), ChatMsg.class);
            return getReplyTextFromResponse(Objects.requireNonNull(apiMsg));
        } catch (Exception e) {
            logger.error("获取思知机器人api失败", e);
        }

        return "不是很懂\n" + "（；´д｀）ゞ";
    }


    /**
     * 这里从api的返回中提取回答，
     * 并且在失败后将回答设置为默认的回答
     *
     * @param chatMsg {@link ChatMsg}
     * @return 从实体对象中提取消息
     * 如果调用状态不为success，返回默认的消息
     */
    private String getReplyTextFromResponse(ChatMsg chatMsg) {
        if (chatMsg.getMessage().equals("success")) {
            return chatMsg.getData().getInfo().getText();
        } else {
            return "不是很懂\n" + "（；´д｀）ゞ";
        }
    }
}

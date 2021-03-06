package com.dingdo.msgHandler.service.impl;

import com.dingdo.extendService.otherService.ServiceFromApi;
import com.dingdo.msgHandler.model.ReqMsg;
import com.dingdo.msgHandler.service.PrivateMsgService;
import com.forte.qqrobot.bot.BotManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrivateMsgServiceImpl implements PrivateMsgService {

    // 使用log4j打印日志
    private static final Logger logger = Logger.getLogger(PrivateMsgServiceImpl.class);

    private final ServiceFromApi serviceFromApi;
    private final BotManager botManager;

    @Autowired
    public PrivateMsgServiceImpl(BotManager botManager, ServiceFromApi serviceFromApi) {
        this.botManager = botManager;
        this.serviceFromApi = serviceFromApi;
    }

    @Override
    public void sendPrivateMsg(String robotId, String userId, String msg) {
        botManager.getBot(robotId)
                .getSender()
                .SENDER
                .sendPrivateMsg(userId, msg);
    }


    @Override
    public String handleMsg(ReqMsg reqMsg) {
        return serviceFromApi.sendMsgFromApi(reqMsg);
    }
}

package com.dingdo.msgHandle.stage

import com.dingdo.robot.mirai.MiraiRobotInitializer
import com.dingdo.user.UserContext
import net.mamoe.mirai.event.events.MessageEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.PostConstruct

@Component
class StageHandler {

    @Autowired
    lateinit var userContext: UserContext

    @PostConstruct
    fun registerMiraiEvent() {
        MiraiRobotInitializer.registeredGroupMsgEvent { msgHandle(it) }
        MiraiRobotInitializer.registeredFriendMsgEvent { msgHandle(it) }
    }

    fun msgHandle(msgEvent: MessageEvent) {
        val user = userContext.getUser(msgEvent.sender.id)
        val info = user.getInfo(UserStageInfo::class)
        info.id = user.id
        val transitionStage = info.stage.transition(msgEvent)
        if (info.stage != transitionStage) {
            info.stage = transitionStage.process(msgEvent)
        }
    }
}

class UserStageInfo {
    var id: Long = -1
    var stage: UserStage = DefaultRootStage.defaultStage
}

@Component
object StageContext : ApplicationContextAware {
    val stageList = ArrayList<UserStage>()
    val rootStageMap = HashMap<UserStage, List<UserStage>>()

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        applicationContext.getBeansOfType(UserStage::class.java).values
            .filter { !Objects.equals(it, DefaultRootStage.defaultStage) }
            .also { stageList.addAll(it) }

        stageList.groupBy { it.rootStage() }
            .also { rootStageMap.putAll(it) }
    }
}
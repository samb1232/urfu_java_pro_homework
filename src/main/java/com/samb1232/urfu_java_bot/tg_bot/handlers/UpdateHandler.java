package com.samb1232.urfu_java_bot.tg_bot.handlers;

import org.springframework.statemachine.StateMachine;

import com.samb1232.urfu_java_bot.dto.UpdateInfo;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotEvent;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotState;


public interface UpdateHandler {
    public abstract void handle(UpdateInfo updateInfo, StateMachine<BotState, BotEvent> stateMachine);
}

package com.samb1232.urfu_java_bot.tg_bot.statemachine;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;

@Service
public class StateMachineService {
    private final StateMachineFactory<BotState, BotEvent> factory;
    private final Map<Long, StateMachine<BotState, BotEvent>> machines = new ConcurrentHashMap<>();

    public StateMachineService(StateMachineFactory<BotState, BotEvent> factory) {
        this.factory = factory;
    }

    public StateMachine<BotState, BotEvent> getStateMachine(Long chatId) {
        return machines.computeIfAbsent(chatId, _ -> {
            StateMachine<BotState, BotEvent> machine = factory.getStateMachine();
            machine.start();
            return machine;
        });
    }
}
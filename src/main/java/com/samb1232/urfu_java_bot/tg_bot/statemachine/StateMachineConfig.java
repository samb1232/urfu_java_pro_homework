package com.samb1232.urfu_java_bot.tg_bot.statemachine;

import java.util.EnumSet;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<BotState, BotEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<BotState, BotEvent> states) throws Exception {
        states.withStates()
            .initial(BotState.MAIN_MENU)
            .states(EnumSet.allOf(BotState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<BotState, BotEvent> transitions) throws Exception {
        BotState[] allStates = BotState.values();
        for (BotState state : allStates) {
            transitions
                .withExternal()
                    .source(state)
                    .target(BotState.MAIN_MENU)
                    .event(BotEvent.START);
        }
        
        for (BotState state : allStates) {
            if (state != BotState.MAIN_MENU) {
                transitions
                    .withExternal()
                        .source(state)
                        .target(BotState.MAIN_MENU)
                        .event(BotEvent.CANCEL);
            }
        }
        
        transitions
            .withExternal()
                .source(BotState.MAIN_MENU)
                .target(BotState.ADD_CAT)
                .event(BotEvent.ADD_CAT_COMMAND)
            .and()
            .withExternal()
                .source(BotState.MAIN_MENU)
                .target(BotState.VIEW_CATS)
                .event(BotEvent.VIEW_CATS_COMMAND)
            .and()
            .withExternal()
                .source(BotState.MAIN_MENU)
                .target(BotState.MY_CATS)
                .event(BotEvent.MY_CATS_COMMAND);
    }
}
package com.samb1232.urfu_java_bot.tg_bot.factories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import com.samb1232.urfu_java_bot.constants.ButtonNames;
import com.samb1232.urfu_java_bot.constants.CallbackData;


@Component
public class KeyboardFactory {

    public InlineKeyboardMarkup createMainMenuKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        
        keyboard.add(createButtonRow(ButtonNames.MY_CATS_BUTTON_TEXT, CallbackData.MY_CATS_CALLBACK));
        keyboard.add(createButtonRow(ButtonNames.VIEW_CATS_BUTTON_TEXT, CallbackData.VIEW_CATS_CALLBACK));
        keyboard.add(createButtonRow(ButtonNames.ADD_CAT_BUTTON_TEXT, CallbackData.ADD_CAT_CALLBACK));
        
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private List<InlineKeyboardButton> createButtonRow(String text, String callbackData) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build());
        return row;
    }

}
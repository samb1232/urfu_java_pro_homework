package com.samb1232.urfu_java_bot.tg_bot.factories;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import com.samb1232.urfu_java_bot.constants.ButtonNames;
import com.samb1232.urfu_java_bot.constants.MenuCallbackData;
import com.samb1232.urfu_java_bot.dto.CatInfo;

public class KeyboardFactory {

    private KeyboardFactory() {
    }

    public static InlineKeyboardMarkup createMainMenuKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        keyboard.add(createButtonRow(ButtonNames.MY_CATS_BUTTON_TEXT, MenuCallbackData.MY_CATS_CALLBACK));
        keyboard.add(createButtonRow(ButtonNames.VIEW_CATS_BUTTON_TEXT, MenuCallbackData.VIEW_CATS_CALLBACK));
        keyboard.add(createButtonRow(ButtonNames.ADD_CAT_BUTTON_TEXT, MenuCallbackData.ADD_CAT_CALLBACK));

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup createBackToMainMenuKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        keyboard.add(createButtonRow(ButtonNames.BACK_TO_MAIN_MENU_BUTTON_TEXT,
                MenuCallbackData.BACK_TO_MAIN_MENU_CALLBACK));

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private static List<InlineKeyboardButton> createButtonRow(String text, String callbackData) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build());
        return row;
    }

    public static InlineKeyboardMarkup createCatsKeyboard(List<CatInfo> cats) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new java.util.ArrayList<>();

        for (CatInfo cat : cats) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(cat.getName());
            button.setCallbackData("view_cat_" + cat.getId());

            List<InlineKeyboardButton> row = new java.util.ArrayList<>();
            row.add(button);
            rows.add(row);
        }

        keyboard.setKeyboard(rows);
        return keyboard;
    }
}
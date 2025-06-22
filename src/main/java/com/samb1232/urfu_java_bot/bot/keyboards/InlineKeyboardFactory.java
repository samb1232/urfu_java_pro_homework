package com.samb1232.urfu_java_bot.bot.keyboards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import com.samb1232.urfu_java_bot.bot.CallbackData;

public class InlineKeyboardFactory {
    public static InlineKeyboardMarkup createStartMenuKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row = Collections.singletonList(
                InlineKeyboardButton.builder()
                        .text("Посмотреть мои картинки")
                        .callbackData(CallbackData.VIEW_IMAGES_CALLBACK)
                        .build()
        );
        keyboard.setKeyboard(Collections.singletonList(row));
        return keyboard;
    }

    public static InlineKeyboardMarkup createImageNavigationKeyboard(int currentIndex, int totalImages) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        
        if (totalImages > 1) {
            buttons.add(InlineKeyboardButton.builder()
                    .text("Далее (" + (currentIndex+1) + "/" + totalImages + ")")
                    .callbackData(CallbackData.NEXT_IMAGE_CALLBACK)
                    .build());
        }
        
        buttons.add(InlineKeyboardButton.builder()
                .text("Назад")
                .callbackData(CallbackData.BACK_TO_START_CALLBACK)
                .build());
        
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(Collections.singletonList(buttons));
        return keyboard;
    }
}

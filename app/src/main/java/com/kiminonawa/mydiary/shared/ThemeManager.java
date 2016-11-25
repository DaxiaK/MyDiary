package com.kiminonawa.mydiary.shared;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.support.v4.media.RatingCompat;

import com.kiminonawa.mydiary.R;

/**
 * Created by daxia on 2016/11/4.
 */

public class ThemeManager {

    public final static int TAKI = 0;
    public final static int MITSUHA = 1;
    public final static int CUSTOM = 2;

    //Default color is TAKI
    public int currentTheme = TAKI;

    private static ThemeManager instance = null;

    private ThemeManager() {
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            synchronized (ThemeManager.class) {
                if (instance == null) {
                    instance = new ThemeManager();
                }
            }
        }
        return instance;
    }

    public void toggleTheme(Context context) {
        switch (currentTheme) {
            case TAKI:
                currentTheme = MITSUHA;
                break;
            case MITSUHA:
                currentTheme = TAKI;
                break;
        }
        SPFManager.setTheme(context, currentTheme);
    }

    public void setCurrentTheme(int themeBySPF) {
        this.currentTheme = themeBySPF;
    }

    public int getCurrentTheme() {
        return currentTheme;
    }

    public int getProfileBgResource() {
        int bgResourceId = R.drawable.profile_theme_bg_taki;
        switch (currentTheme) {
            case TAKI:
                bgResourceId = R.drawable.profile_theme_bg_taki;
                break;
            case MITSUHA:
                bgResourceId = R.drawable.profile_theme_bg_mitsuha;
                break;
        }
        return bgResourceId;
    }

    public int getTopicItemSelectResource() {
        int bgResourceId = R.drawable.main_topic_item_selector_taki;
        switch (currentTheme) {
            case TAKI:
                bgResourceId = R.drawable.main_topic_item_selector_taki;
                break;
            case MITSUHA:
                bgResourceId = R.drawable.main_topic_item_selector_mitsuha;
                break;
        }
        return bgResourceId;
    }

    public int getEntriesBgResource() {
        int bgResourceId = R.drawable.theme_bg_taki;
        switch (currentTheme) {
            case TAKI:
                bgResourceId = R.drawable.theme_bg_taki;
                break;
            case MITSUHA:
                bgResourceId = R.drawable.theme_bg_mitsuha;
                break;
        }
        return bgResourceId;
    }


    public int getButtonBgResource() {
        int bgResourceId = R.drawable.button_bg_taki;
        switch (currentTheme) {
            case TAKI:
                bgResourceId = R.drawable.button_bg_taki;
                break;
            case MITSUHA:
                bgResourceId = R.drawable.button_bg_mitsuha;
                break;
        }
        return bgResourceId;
    }

    public int getContactsBgResource() {
        int bgResourceId = R.drawable.contacts_bg_taki;
        switch (currentTheme) {
            case TAKI:
                bgResourceId = R.drawable.contacts_bg_taki;
                break;
            case MITSUHA:
                bgResourceId = R.drawable.contacts_bg_mitsuha;
                break;
        }
        return bgResourceId;
    }

    public int getThemeDarkColor(Context context) {
        int darkColor = R.color.theme_dark_color_taki;
        switch (currentTheme) {
            case TAKI:
                darkColor = R.color.theme_dark_color_taki;
                break;
            case MITSUHA:
                darkColor = R.color.theme_dark_color_mistuha;
                break;
        }
        return ColorTools.getColor(context, darkColor);
    }

    public int getThemeMainColor(Context context) {
        int mainColor = R.color.themeColor_taki;
        switch (currentTheme) {
            case TAKI:
                mainColor = R.color.themeColor_taki;
                break;
            case MITSUHA:
                mainColor = R.color.themeColor_mistuha;
                break;
        }

        return ColorTools.getColor(context, mainColor);
    }

    public String getThemeUserName(Context context) {
        String userName = context.getString(R.string.profile_username_taki);
        switch (currentTheme) {
            case TAKI:
                userName = context.getString(R.string.profile_username_taki);
                break;
            case MITSUHA:
                userName = context.getString(R.string.profile_username_mitsuha);
                break;
        }
        return userName;
    }

    public
    @RatingCompat.Style
    int getDatePickerStyle() {
        int style;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            style = AlertDialog.THEME_HOLO_LIGHT;
        } else {
            style = R.style.TakiDatePickerDialogTheme;
            switch (currentTheme) {
                case TAKI:
                    style = R.style.TakiDatePickerDialogTheme;
                    break;
                case MITSUHA:
                    style = R.style.MistuhaDatePickerDialogTheme;
                    break;
            }
        }
        return style;
    }


}

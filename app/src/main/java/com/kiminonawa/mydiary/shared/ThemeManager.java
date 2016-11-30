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

    public void saveTheme(Context context, int themeId) {
        SPFManager.setTheme(context, themeId);
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

    /**
     * This color also is secondary color.
     * @param context
     * @return
     */
    public int getThemeDarkColor(Context context) {
        int darkColor;
        switch (currentTheme) {
            case TAKI:
                darkColor = ColorTools.getColor(context, R.color.theme_dark_color_taki);
                break;
            case MITSUHA:
                darkColor = ColorTools.getColor(context, R.color.theme_dark_color_mistuha);
                break;
            default:
                darkColor = SPFManager.getSecondaryColor(context);
                break;
        }
        return darkColor;
    }

    public int getThemeMainColor(Context context) {
        int mainColor;
        switch (currentTheme) {
            case TAKI:
                mainColor = ColorTools.getColor(context, R.color.themeColor_taki);
                break;
            case MITSUHA:
                mainColor = ColorTools.getColor(context, R.color.themeColor_mistuha);
                break;
            default:
                mainColor = SPFManager.getMainColor(context);
                break;
        }
        return mainColor;
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

package com.kiminonawa.mydiary.shared;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
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

    public final static String CUSTOM_PROFILE_BANNER_BG_FILENAME = "custom_profile_banner_bg";

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

    public Drawable getProfileBgDrawable(Context context) {
        Drawable bgDrawable;
        switch (currentTheme) {
            case TAKI:
                bgDrawable = ViewTools.getDrawable(context, R.drawable.profile_theme_bg_taki);
                break;
            case MITSUHA:
                bgDrawable = ViewTools.getDrawable(context, R.drawable.profile_theme_bg_mitsuha);
                break;
            default:
                bgDrawable = Drawable.createFromPath(new FileManager(context).getDiaryDir().getPath() + "/" + CUSTOM_PROFILE_BANNER_BG_FILENAME);
                if (bgDrawable == null) {
                    bgDrawable = new ColorDrawable(getThemeMainColor(context));
                }
                break;
        }
        return bgDrawable;
    }

    public Drawable getTopicItemSelectDrawable(Context context) {
        return createTopicItemSelectBg(context);
    }

    private Drawable createTopicItemSelectBg(Context context) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed},
                new ColorDrawable(getThemeMainColor(context)));
        stateListDrawable.addState(new int[]{},
                new ColorDrawable(Color.WHITE));
        return stateListDrawable;
    }

    public Drawable getEntriesBgDrawable(Context context) {
        Drawable bgDrawable;
        switch (currentTheme) {
            case TAKI:
                bgDrawable = ViewTools.getDrawable(context, R.drawable.theme_bg_taki);
                break;
            case MITSUHA:
                bgDrawable = ViewTools.getDrawable(context, R.drawable.theme_bg_taki);
                break;
            //TODO make default bg for custom
            default:
                bgDrawable = new ColorDrawable(SPFManager.getMainColor(context));
                break;
        }
        return bgDrawable;
    }


    public Drawable getButtonBgDrawable(Context context) {
        return createButtonCustomBg(context);
    }

    /**
     * Create the custom button programmatically
     *
     * @param context
     * @return
     */
    private Drawable createButtonCustomBg(Context context) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, createCustomDrawable(context));
        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled},
                ViewTools.getDrawable(context, R.drawable.button_bg_disable));
        stateListDrawable.addState(new int[]{},
                ViewTools.getDrawable(context, R.drawable.button_bg_n));
        return stateListDrawable;
    }

    /**
     * The Custom button press drawable
     *
     * @param context
     * @return
     */
    private Drawable createCustomDrawable(Context context) {
        int padding = ScreenHelper.dpToPixel(context.getResources(), 5);
        int mainColorCode = ThemeManager.getInstance().getThemeMainColor(context);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.getPadding(new Rect(padding, padding, padding, padding));
        gradientDrawable.setCornerRadius(ScreenHelper.dpToPixel(context.getResources(), 3));
        gradientDrawable.setStroke(ScreenHelper.dpToPixel(context.getResources(), 1), mainColorCode);
        gradientDrawable.setColor(mainColorCode);
        return gradientDrawable;
    }


    public Drawable getContactsBgDrawable(Context context) {
        Drawable bgDrawable;
        switch (currentTheme) {
            case TAKI:
                bgDrawable = ViewTools.getDrawable(context, R.drawable.contacts_bg_taki);
                break;
            case MITSUHA:
                bgDrawable = ViewTools.getDrawable(context, R.drawable.contacts_bg_taki);
                break;
            //TODO make default bg for custom
            default:
                bgDrawable = new ColorDrawable(SPFManager.getMainColor(context));
                break;
        }
        return bgDrawable;
    }

    /**
     * This color also is secondary color.
     *
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
        String userName;
        switch (currentTheme) {
            case TAKI:
                userName = context.getString(R.string.profile_username_taki);
                break;
            case MITSUHA:
                userName = context.getString(R.string.profile_username_mitsuha);
                break;
            default:
                userName = context.getString(R.string.your_name_is);
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

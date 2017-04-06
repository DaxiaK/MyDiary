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
import com.kiminonawa.mydiary.main.topic.ITopic;
import com.kiminonawa.mydiary.shared.statusbar.ChinaPhoneHelper;

import java.io.File;

/**
 * Created by daxia on 2016/11/4.
 */

public class ThemeManager {

    public final static int TAKI = 0;
    public final static int MITSUHA = 1;
    public final static int CUSTOM = 2;

    public final static String CUSTOM_PROFILE_BANNER_BG_FILENAME = "custom_profile_banner_bg";
    public final static String CUSTOM_PROFILE_PICTURE_FILENAME = "custom_profile_picture_bg";
    public final static String CUSTOM_TOPIC_BG_FILENAME = "custom_topic_bg";


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

    public int getTopicBgWidth(Context context) {
        return ScreenHelper.getScreenWidth(context);
    }

    public int getTopicBgHeight(Context context) {
        int topbarHeight = context.getResources().getDimensionPixelOffset(R.dimen.top_bar_height);
        int bgHeight;
        if (ChinaPhoneHelper.getDeviceStatusBarType() == ChinaPhoneHelper.OTHER) {
            bgHeight = ScreenHelper.getScreenHeight(context) -
                    ScreenHelper.getStatusBarHeight(context) -
                    //diary activity top bar  + edit bottom bar
                    ScreenHelper.dpToPixel(context.getResources(), 40) - topbarHeight;
        } else {
            bgHeight = ScreenHelper.getScreenHeight(context) -
                    //diary activity top bar  + edit bottom bar
                    ScreenHelper.dpToPixel(context.getResources(), 40) - topbarHeight;
        }
        return bgHeight;
    }

    public int getTopicBgWithoutEditBarHeight(Context context) {
        int topbarHeight = context.getResources().getDimensionPixelOffset(R.dimen.top_bar_height);
        int withoutEditBarHeight = ScreenHelper.getScreenHeight(context) -
                //diary activity top bar
                topbarHeight;
        return withoutEditBarHeight;
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
                FileManager settingFM = new FileManager(context, FileManager.SETTING_DIR);
                File profileBgFile = new File(settingFM.getDirAbsolutePath()
                        + "/" + CUSTOM_PROFILE_BANNER_BG_FILENAME);
                if (profileBgFile.exists()) {
                    bgDrawable = Drawable.createFromPath(profileBgFile.getAbsolutePath());
                } else {
                    bgDrawable = new ColorDrawable(getThemeMainColor(context));
                }
                break;
        }
        return bgDrawable;
    }

    public Drawable getProfilePictureDrawable(Context context) {
        Drawable pictureDrawable;
        try {
            FileManager settingFM= new FileManager(context, FileManager.SETTING_DIR);
            File pictureFile = new File(settingFM.getDirAbsolutePath()
                    + "/" + CUSTOM_PROFILE_PICTURE_FILENAME);
            if (pictureFile.exists()) {
                pictureDrawable = Drawable.createFromPath(pictureFile.getAbsolutePath());
            } else {
                pictureDrawable = ViewTools.getDrawable(context, R.drawable.ic_person_picture_default);
            }
        } catch (NullPointerException e) {
            pictureDrawable = ViewTools.getDrawable(context, R.drawable.ic_person_picture_default);
        }
        return pictureDrawable;
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

    /**
     * Any theme using the same topic bg , if it exist.
     *
     * @param context
     * @param topicId
     * @param topicType
     * @return
     */
    public Drawable getTopicBgDrawable(Context context, long topicId, int topicType) {
        Drawable returnDrawable;
        switch (topicType) {
            case ITopic.TYPE_MEMO:
                returnDrawable = getMemoBgDrawable(context, topicId);
                break;
            case ITopic.TYPE_CONTACTS:
                returnDrawable = getContactsBgDrawable(context, topicId);
                break;
            //ITopic.TYPE_DIARY
            default:
                returnDrawable = getEntriesBgDrawable(context, topicId);
                break;
        }
        return returnDrawable;
    }

    public Drawable getEntriesBgDrawable(Context context, long topicId) {
        Drawable bgDrawable;
        FileManager diaryFM = new FileManager(context, FileManager.DIARY_ROOT_DIR);
        File entriesBg = new File(
                diaryFM.getDirAbsolutePath()
                        + "/" + topicId
                        + "/" + CUSTOM_TOPIC_BG_FILENAME);
        if (entriesBg.exists()) {
            bgDrawable = Drawable.createFromPath(entriesBg.getAbsolutePath());
        } else {
            switch (currentTheme) {
                case TAKI:
                    bgDrawable = ViewTools.getDrawable(context, R.drawable.theme_bg_taki);
                    break;
                case MITSUHA:
                    bgDrawable = ViewTools.getDrawable(context, R.drawable.theme_bg_mitsuha);
                    break;
                default:
                    bgDrawable = new ColorDrawable(SPFManager.getMainColor(context));
                    break;
            }
        }
        return bgDrawable;
    }

    public Drawable getMemoBgDrawable(Context context, long topicId) {
        Drawable bgDrawable;
        FileManager memoFM = new FileManager(context, FileManager.MEMO_ROOT_DIR);
        File memoBg = new File(
                memoFM.getDirAbsolutePath()
                        + "/" + topicId
                        + "/" + CUSTOM_TOPIC_BG_FILENAME);
        if (memoBg.exists()) {
            bgDrawable = Drawable.createFromPath(memoBg.getAbsolutePath());
        } else {
            switch (currentTheme) {
                case TAKI:
                    bgDrawable = new ColorDrawable(Color.WHITE);
                    break;
                case MITSUHA:
                    bgDrawable = new ColorDrawable(Color.WHITE);
                    break;
                default:
                    bgDrawable = new ColorDrawable(Color.WHITE);
                    break;
            }
        }
        return bgDrawable;
    }

    public Drawable getContactsBgDrawable(Context context, long topicId) {
        Drawable bgDrawable;
        FileManager contactsFM = new FileManager(context, FileManager.CONTACTS_ROOT_DIR);
        File contactsBg = new File(
                contactsFM.getDirAbsolutePath()
                        + "/" + topicId
                        + "/" + CUSTOM_TOPIC_BG_FILENAME);
        if (contactsBg.exists()) {
            bgDrawable = Drawable.createFromPath(contactsBg.getAbsolutePath());
        } else {
            switch (currentTheme) {
                case TAKI:
                    bgDrawable = ViewTools.getDrawable(context, R.drawable.contacts_bg_taki);
                    break;
                case MITSUHA:
                    bgDrawable = ViewTools.getDrawable(context, R.drawable.contacts_bg_mitsuha);
                    break;
                default:
                    bgDrawable = new ColorDrawable(SPFManager.getMainColor(context));
                    break;
            }
        }
        return bgDrawable;
    }

    public Drawable getTopicBgDefaultDrawable(Context context, int topicType) {
        Drawable returnDefaultDrawable;
        switch (topicType) {
            case ITopic.TYPE_MEMO:
                returnDefaultDrawable = getMemoBgDefaultDrawable();
                break;
            case ITopic.TYPE_CONTACTS:
                returnDefaultDrawable = getContactsDefaultBgDrawable(context);
                break;
            //ITopic.TYPE_DIARY
            default:
                returnDefaultDrawable = getEntriesBgDefaultDrawable(context);
                break;
        }
        return returnDefaultDrawable;
    }

    private Drawable getEntriesBgDefaultDrawable(Context context) {
        Drawable defaultBgDrawable;
        switch (currentTheme) {
            case TAKI:
                defaultBgDrawable = ViewTools.getDrawable(context, R.drawable.theme_bg_taki);
                break;
            case MITSUHA:
                defaultBgDrawable = ViewTools.getDrawable(context, R.drawable.theme_bg_mitsuha);
                break;
            default:
                defaultBgDrawable = new ColorDrawable(SPFManager.getMainColor(context));
                break;
        }
        return defaultBgDrawable;
    }

    public Drawable getMemoBgDefaultDrawable() {
        Drawable defaultBgDrawable;
        switch (currentTheme) {
            case TAKI:
                defaultBgDrawable = new ColorDrawable(Color.WHITE);
                break;
            case MITSUHA:
                defaultBgDrawable = new ColorDrawable(Color.WHITE);
                break;
            default:
                defaultBgDrawable = new ColorDrawable(Color.WHITE);
                break;
        }
        return defaultBgDrawable;
    }

    public Drawable getContactsDefaultBgDrawable(Context context) {
        Drawable defaultBgDrawable;
        switch (currentTheme) {
            case TAKI:
                defaultBgDrawable = ViewTools.getDrawable(context, R.drawable.contacts_bg_taki);
                break;
            case MITSUHA:
                defaultBgDrawable = ViewTools.getDrawable(context, R.drawable.contacts_bg_mitsuha);
                break;
            default:
                defaultBgDrawable = new ColorDrawable(SPFManager.getMainColor(context));
                break;
        }
        return defaultBgDrawable;
    }


    public Drawable getButtonBgDrawable(Context context) {
        return createButtonCustomBg(context);
    }

    public Drawable createDiaryViewerInfoBg(Context context) {
        int dp10 = ScreenHelper.dpToPixel(context.getResources(), 10);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(getThemeMainColor(context));
        shape.setCornerRadii(new float[]{dp10, dp10, dp10, dp10, 0, 0, 0, 0});
        return shape;
    }

    public Drawable createDiaryViewerEditBarBg(Context context) {
        int dp10 = ScreenHelper.dpToPixel(context.getResources(), 10);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(getThemeMainColor(context));
        shape.setCornerRadii(new float[]{0, 0, 0, 0, dp10, dp10, dp10, dp10});
        return shape;
    }

    /**
     * Create the custom button programmatically
     *
     * @param context
     * @return
     */
    private Drawable createButtonCustomBg(Context context) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, createCustomPressedDrawable(context));
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
    private Drawable createCustomPressedDrawable(Context context) {
        int padding = ScreenHelper.dpToPixel(context.getResources(), 5);
        int mainColorCode = ThemeManager.getInstance().getThemeMainColor(context);
        int boardColor = ColorTools.getColor(context, R.color.button_boarder_color);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.getPadding(new Rect(padding, padding, padding, padding));
        gradientDrawable.setCornerRadius(ScreenHelper.dpToPixel(context.getResources(), 3));
        gradientDrawable.setStroke(ScreenHelper.dpToPixel(context.getResources(), 1), boardColor);
        gradientDrawable.setColor(mainColorCode);
        return gradientDrawable;
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

    public File getTopicBgSavePathFile(Context context, long topicId, int topicType) {
        File outputFile;
        switch (topicType) {
            case ITopic.TYPE_MEMO:
                FileManager memoFM = new FileManager(context, FileManager.MEMO_ROOT_DIR);
                outputFile = new File(
                        memoFM.getDirAbsolutePath()
                                + "/" + topicId
                                + "/" + ThemeManager.CUSTOM_TOPIC_BG_FILENAME);
                break;
            case ITopic.TYPE_CONTACTS:
                FileManager contactsFM = new FileManager(context, FileManager.CONTACTS_ROOT_DIR);
                outputFile = new File(
                        contactsFM.getDirAbsolutePath()
                                + "/" + topicId
                                + "/" + ThemeManager.CUSTOM_TOPIC_BG_FILENAME);
                break;
            //TYPE_DIARY
            default:
                FileManager diaryFM = new FileManager(context, FileManager.DIARY_ROOT_DIR);
                outputFile = new File(
                        diaryFM.getDirAbsolutePath()
                                + "/" + topicId
                                + "/" + ThemeManager.CUSTOM_TOPIC_BG_FILENAME);
                break;
        }
        return outputFile;
    }


    public
    @RatingCompat.Style
    int getPickerStyle() {
        int style;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            style = AlertDialog.THEME_HOLO_LIGHT;
        } else {
            switch (currentTheme) {
                case TAKI:
                    style = R.style.TakiPickerDialogTheme;
                    break;
                case MITSUHA:
                    style = R.style.MistuhaPickerDialogTheme;
                    break;
                default:
                    //Use the system color
                    style = R.style.CustomPickerDialogTheme;
            }
        }
        return style;
    }


}

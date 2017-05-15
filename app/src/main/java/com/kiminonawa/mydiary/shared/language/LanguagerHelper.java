package com.kiminonawa.mydiary.shared.language;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import com.kiminonawa.mydiary.shared.SPFManager;

import java.util.Locale;

/**
 * Created by daxia on 2017/5/15.
 */

public class LanguagerHelper {


    public static Locale getLocaleLanguage(Context context) {
        Locale locale;
        switch (SPFManager.getLocalLanguageCode(context)) {
            case 1:
                locale = Locale.ENGLISH;
                break;
            case 2:
                locale = Locale.JAPANESE;
                break;
            case 3:
                locale = Locale.TRADITIONAL_CHINESE;
                break;
            case 4:
                locale = Locale.SIMPLIFIED_CHINESE;
                break;
            case 5:
                locale = Locale.KOREAN;
                break;
            case 6:
                locale = new Locale("th", "");
                break;
            case 7:
                locale = Locale.FRENCH;
                break;
            case 8:
                locale = new Locale("es", "");
                break;
            // 0 = default = language of system
            default:
                Configuration config = context.getResources().getConfiguration();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    locale = MyContextWrapper.getSystemLocale(config);
                } else {
                    locale = MyContextWrapper.getSystemLocaleLegacy(config);
                }
                break;
        }
        return locale;
    }
}

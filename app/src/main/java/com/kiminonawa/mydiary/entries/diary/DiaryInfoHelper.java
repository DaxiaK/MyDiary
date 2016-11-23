package com.kiminonawa.mydiary.entries.diary;

import com.kiminonawa.mydiary.R;

/**
 * Created by daxia on 2016/10/31.
 */

public class DiaryInfoHelper {

    public final static int WEATHER_SIZE = 6;
    public final static int WEATHER_SUNNY = 0;
    public final static int WEATHER_CLOUD = 1;
    public final static int WEATHER_WINDY = 2;
    public final static int WEATHER_RAINY = 3;
    public final static int WEATHER_SNOWY = 4;
    public final static int WEATHER_FOGGY = 5;


    public final static int MOOD_SIZE = 3;
    public final static int MOOD_HAPPY = 0;
    public final static int MOOD_SOSO = 1;
    public final static int MOOD_UNHAPPY = 2;


    /**
     * Weather
     */
    public static int getWeatherResourceId(int weather) {
        int weatherResourceId;
        switch (weather) {
            default:
                weatherResourceId = R.drawable.ic_weather_sunny;
                break;
            case WEATHER_CLOUD:
                weatherResourceId = R.drawable.ic_weather_cloud;
                break;
            case WEATHER_WINDY:
                weatherResourceId = R.drawable.ic_weather_windy;
                break;
            case WEATHER_RAINY:
                weatherResourceId = R.drawable.ic_weather_rainy;
                break;
            case WEATHER_SNOWY:
                weatherResourceId = R.drawable.ic_weather_snowy;
                break;
            case WEATHER_FOGGY:
                weatherResourceId = R.drawable.ic_weather_foggy;
                break;
        }
        return weatherResourceId;
    }


    public static Integer[] getWeatherArray() {
        return new Integer[]{R.drawable.ic_weather_sunny, R.drawable.ic_weather_cloud,
                R.drawable.ic_weather_windy, R.drawable.ic_weather_rainy, R.drawable.ic_weather_snowy,
                R.drawable.ic_weather_foggy};
    }


    /**
     * Mood
     */
    public static int getMoodResourceId(int mood) {
        int moodResourceId;
        switch (mood) {
            default:
                moodResourceId = R.drawable.ic_mood_happy;
                break;
            case MOOD_SOSO:
                moodResourceId = R.drawable.ic_mood_soso;
                break;
            case MOOD_UNHAPPY:
                moodResourceId = R.drawable.ic_mood_unhappy;
                break;
        }
        return moodResourceId;
    }

    public static Integer[] getMoodArray() {
        return new Integer[]{R.drawable.ic_mood_happy, R.drawable.ic_mood_soso,
                R.drawable.ic_mood_unhappy};
    }


}

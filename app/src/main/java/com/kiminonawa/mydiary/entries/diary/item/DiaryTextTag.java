package com.kiminonawa.mydiary.entries.diary.item;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by daxia on 2016/11/21.
 * To avoid getting the error focus , and send more data in one method.
 */
public class DiaryTextTag implements Parcelable {

    private int positionTag;
    private int edittextIndex;
    private String nextEditTextStr;

    public DiaryTextTag(int positionTag) {
        this.positionTag = positionTag;
    }

    protected DiaryTextTag(Parcel in) {
        positionTag = in.readInt();
        edittextIndex = in.readInt();
        nextEditTextStr = in.readString();
    }

    public int getPositionTag() {
        return positionTag;
    }

    public void setPositionTag(int positionTag) {
        this.positionTag = positionTag;
    }

    public String getNextEditTextStr() {
        return nextEditTextStr;
    }

    public void setNextEditTextStr(String nextEditTextStr) {
        this.nextEditTextStr = nextEditTextStr;
    }

    public int getEdittextIndex() {
        return edittextIndex;
    }

    public void setEdittextIndex(int edittextIndex) {
        this.edittextIndex = edittextIndex;
    }

    /*
     * Parcel
     */

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(positionTag);
        dest.writeInt(edittextIndex);
        dest.writeString(nextEditTextStr);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DiaryTextTag> CREATOR = new Creator<DiaryTextTag>() {
        @Override
        public DiaryTextTag createFromParcel(Parcel in) {
            return new DiaryTextTag(in);
        }

        @Override
        public DiaryTextTag[] newArray(int size) {
            return new DiaryTextTag[size];
        }
    };


}

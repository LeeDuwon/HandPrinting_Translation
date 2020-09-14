package com.example.csr.entity;

import android.os.Parcel;
import android.os.Parcelable;

/*
Parcelable 타입의 객체만 Intent 을 통해 Activity 간 데이터를 넘길 수 있음
 */
public class WordItem implements Parcelable  {

    public String id;           // Word Doc ID
    public Word word;           // Word 객체

    public WordItem(Parcel in) {
        readFromParcel(in);
    }

    public WordItem(String id, Word word) {
        this.id = id;
        this.word = word;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeParcelable(this.word, flags);
    }

    private void readFromParcel(Parcel in){
        this.id = in.readString();
        this.word = in.readParcelable(Word.class.getClassLoader());
    }

    public static final Creator CREATOR = new Creator() {
        public WordItem createFromParcel(Parcel in) {
            return new WordItem(in);
        }

        public WordItem[] newArray(int size) {
            return new WordItem[size];
        }
    };
}

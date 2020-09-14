package com.example.csr.entity;

import android.os.Parcel;
import android.os.Parcelable;

/*
Parcelable 타입의 객체만 Intent 을 통해 Activity 간 데이터를 넘길 수 있음
 */
public class Word implements Parcelable {

    private String word;                        // 단어
    private String translatedWord;              // 번역된 단어
    private String language;                    // 한글(KOR) / 영어(ENG)

    public boolean favorite;                    // 즐겨찾기

    private long createTimeMillis;              // 생성일시를 millisecond 로 표현

    public Word() {}

    public Word(Parcel in) {
        readFromParcel(in);
    }

    public Word(String word, String translatedWord, String language, long createTimeMillis) {
        this.word = word;
        this.translatedWord = translatedWord;
        this.language = language;
        this.createTimeMillis = createTimeMillis;
    }

    public String getWord() {
        return word;
    }

    public String getTranslatedWord() {
        return translatedWord;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public long getCreateTimeMillis() {
        return createTimeMillis;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.word);
        dest.writeString(this.translatedWord);
        dest.writeString(this.language);
        dest.writeByte((byte) (this.favorite ? 1 : 0));
        dest.writeLong(this.createTimeMillis);
    }

    private void readFromParcel(Parcel in){
        this.word = in.readString();
        this.translatedWord = in.readString();
        this.language = in.readString();
        this.favorite = in.readByte() != 0;
        this.createTimeMillis = in.readLong();
    }

    public static final Creator CREATOR = new Creator() {
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        public Word[] newArray(int size) {
            return new Word[size];
        }
    };
}

package com.example.csr.entity;

public class Word {

    private String word;                        // 단어
    private String translatedWord;              // 번역된 단어
    private String language;                    // 한글(KOR) / 영어(ENG)

    public boolean favorite;                    // 즐겨찾기

    private long createTimeMillis;              // 생성일시를 millisecond 로 표현

    public Word() {}

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
}

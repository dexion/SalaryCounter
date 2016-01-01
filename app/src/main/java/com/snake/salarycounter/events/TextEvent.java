package com.snake.salarycounter.events;

public class TextEvent {

    public int mTextEditId;
    public String mValue;
    public Long mId;

    public TextEvent(int textEditId, String value, Long _id) {
        mTextEditId = textEditId;
        mValue = value;
        mId = _id;
    }
}

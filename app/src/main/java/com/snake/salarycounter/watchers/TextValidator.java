package com.snake.salarycounter.watchers;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.snake.salarycounter.R;
import com.snake.salarycounter.events.TextEvent;

import de.greenrobot.event.EventBus;

public class TextValidator implements TextWatcher {
    private final TextView textView;
    private final Context context;
    private final Long mId;

    public TextValidator(Context context, TextView textView, long _id) {
        this.textView = textView;
        this.context = context;
        this.mId = _id;
    }

    public void validate(TextView textView, String text) {
        try {
            if (text.isEmpty()) {
                textView.setError(context.getResources().getString(R.string.cannot_blank));
/*            } else if (Double.valueOf(text) < 0) {
                textView.setError(context.getResources().getString(R.string.incorrect_value));*/
            } else {
                EventBus.getDefault().post(new TextEvent(textView.getId(), text, mId));
            }
        } catch (Exception e) {

        }
    }

    @Override
    final public void afterTextChanged(Editable s) {
        String text = textView.getText().toString();
        validate(textView, text);
    }

    @Override
    final public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Don't care */ }

    @Override
    final public void onTextChanged(CharSequence s, int start, int before, int count) { /* Don't care */ }
}

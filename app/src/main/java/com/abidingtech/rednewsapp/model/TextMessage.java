package com.abidingtech.rednewsapp.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abidingtech.rednewsapp.R;
import com.abidingtech.rednewsapp.callback.ActionCallback;

public class TextMessage extends Message{

    private TextView tvMsgBody;

    public TextMessage(Context context) {
        super(context);
    }

    @Override
    public View loadView(ViewGroup viewGroup) {

        final View centerView = inflater.inflate(R.layout.text_center, null, false);
        tvMsgBody = centerView.findViewById(R.id.tvBody);
        llCenter.addView(centerView);
        return null;
    }

    @Override
    public void loadData() {

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvMsgBody.getLayoutParams();

        //"\u00A0" is non line break space unicode
        // tvMsgBody.setText(chat.body+"\u00A0"+chat.getFriendlyTime());
        tvMsgBody.setText(chat.getBody());
//        if(chat.is_deleted){
//            tvMsgBody.setTextColor(context.getResources().getColor(R.color.gray_text));
//            tvMsgBody.setTypeface(null, Typeface.ITALIC);
//        }
//        else {
//            tvMsgBody.setTextColor(context.getResources().getColor(R.color.black));
//            tvMsgBody.setTypeface(null, Typeface.NORMAL);
//        }
        tvMsgBody.measure(0, 0);

        if (tvMsgBody.getMeasuredWidth() > MAX_WIDTH) {
            params.width = MAX_WIDTH;
        } else {
            params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
        tvMsgBody.setLayoutParams(params);
    }

    @Override
    protected void create() {

        if(chat.getBody() == null){
            throw new NullPointerException("Message body cannot be null");
        }
        if(chat.getBody().isEmpty()){
            throw new IllegalStateException("Message body cannot be empty");
        }
        chat.media_type_id = TEXT_TYPE;
        saveMessage(new ActionCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(String msg) {

            }
        });

    }
}

package com.abidingtech.rednewsapp.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abidingtech.rednewsapp.R;
import com.abidingtech.rednewsapp.callback.ActionCallback;

public class ActionMessage extends Message{

    private TextView tvBody;

    public ActionMessage(Context context) {
        super(context);
    }

    @Override
    public View loadView(ViewGroup viewGroup) {

        final View centerView = inflater.inflate(R.layout.action_center, viewGroup, false);
        tvBody = centerView.findViewById(R.id.tvBody);
        return centerView;
    }

    @Override
    public void loadData() {

        //"\u00A0" is non line break space unicode
        // tvMsgBody.setText(chat.body+"\u00A0"+chat.getFriendlyTime());
        tvBody.setText(chat.getBody());
    }

    @Override
    protected void create() {

    }
}

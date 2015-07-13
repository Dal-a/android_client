package com.eattle.phoket.Card;

import android.content.Context;

import com.dexafree.materialList.cards.SimpleCard;
import com.eattle.phoket.R;

/**
 * Created by GA on 2015. 5. 14..
 */
public class DailyCard extends SimpleCard {
    private String dailyImage;
//    private OnButtonPressListener mListener;


    public DailyCard(Context context) {
        super(context);
        this.setSelectable(true);
    }

    public String getDailyImage() {
        return dailyImage;
    }

    public void setDailyImage(String dailyImage) {
        this.dailyImage = dailyImage;
    }
    /*

    public OnButtonPressListener getmListener() {
        return mListener;
    }

    public void setmListener(OnButtonPressListener mListener) {
        this.mListener = mListener;
    }

    public OnButtonPressListener getOnButtonPressedListener() {
        return mListener;
    }

    public void setOnButtonPressedListener(OnButtonPressListener mListener) {
        this.mListener = mListener;
    }
*/
    @Override
    public int getLayout(){
        return R.layout.material_daily_card;
    }
}

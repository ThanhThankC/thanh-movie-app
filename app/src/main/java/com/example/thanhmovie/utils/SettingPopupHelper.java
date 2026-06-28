package com.example.thanhmovie.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioGroup;

import com.example.thanhmovie.R;

public class SettingPopupHelper {
    public static void showSettingsPopup(Context context, View anchorView){
        LayoutInflater inflater = LayoutInflater.from(context);
        View popupView = inflater.inflate(R.layout.dialog_settings_popup,(ViewGroup) anchorView.getRootView(), false);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                500,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        RadioGroup rgLang = popupView.findViewById(R.id.rg_lang);
        String currentLang = LocaleManager.getLocale(context);
        rgLang.check(currentLang.equals("en") ? R.id.rb_en : R.id.rb_vi);

        rgLang.setOnCheckedChangeListener(((group, checkedId) -> {
            String newLang = (checkedId == R.id.rb_en) ? "en" : "vi";

            if (!newLang.equals(currentLang)){
                LocaleManager.setLocale(context, newLang);
                popupWindow.dismiss();
                ((Activity) context).recreate();
            }
        }));

        popupWindow.showAsDropDown(anchorView, -390, -10);
    }
}

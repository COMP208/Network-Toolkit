package com.example.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

/**
 * last update: 2020/4/12
 * by: Weihao.Jin
 */

public class UI_control {


    /**
     * set the customer action bar
     * @param activity target activity
     * @param layoutId action bar's layout id
     */
    public static void setCustomActionBar(AppCompatActivity activity,int layoutId) {
        androidx.appcompat.app.ActionBar.LayoutParams lp = new androidx.appcompat.app.ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        LayoutInflater inflator = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mActionBarView = inflator.inflate(layoutId,null);
        androidx.appcompat.app.ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setCustomView(mActionBarView, lp);
        actionBar.setDisplayOptions(actionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    /**
     * set the clear button for input box
     * @param input the input box (editText)
     * @param clear the corresponding imageButton (clear button)
     */
    public static void editTextListener(final EditText input, final ImageButton clear) {
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = charSequence.toString();
                clearButtonControl(str, input, clear);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = charSequence.toString();
                clearButtonControl(str, input, clear);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                clearButtonControl(str, input, clear);
            }
        });

    }

    /**
     * empty the input box when user clicking the clear button
     * @param str the current content
     * @param input the input box (editText)
     * @param clear the corresponding imageButton (clear button)
     */
    public static void clearButtonControl(String str, final EditText input, final ImageButton clear){
        if (TextUtils.isEmpty(str)) {
            clear.setVisibility(View.INVISIBLE);
        } else {
            clear.setVisibility(View.VISIBLE);
            clear.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    input.setText("");
                    clear.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    /**
     * hide the soft keyboard in current activity
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}

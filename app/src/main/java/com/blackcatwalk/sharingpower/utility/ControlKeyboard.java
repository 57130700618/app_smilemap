package com.blackcatwalk.sharingpower.utility;


import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class ControlKeyboard {

    public void hideKeyboard(Activity _activity) {
        if (_activity.getCurrentFocus() != null) {
            InputMethodManager _inputMethodManager = (InputMethodManager) _activity.getSystemService(INPUT_METHOD_SERVICE);
            _inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
        }
    }

    public static void hideKeyboardWhenShowViewPage(Activity _activity) {
        _activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void hideKeyboardWhenFocusEdittext(Activity _activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) _activity.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(_activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUIHidenKeyboardWhenClickOutEdittext(View _view, final Activity _activity) {

        if (!(_view instanceof Spinner)) {
            //Set up touch listener for non-text box views to hide keyboard.
            if (!(_view instanceof EditText)) {
                _view.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        hideKeyboardWhenFocusEdittext(_activity);
                        return false;
                    }
                });
            }
            //If a layout container, iterate over children and seed recursion.
            if (_view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) _view).getChildCount(); i++) {
                    View _innerView = ((ViewGroup) _view).getChildAt(i);
                    setupUIHidenKeyboardWhenClickOutEdittext(_innerView, _activity);
                }
            }
        }
    }

    //---------------------- Show Keyboard Edittext ----------------------//

    public static void showKeyboard(Activity _activity) {
        InputMethodManager _inputMethodManager = (InputMethodManager) _activity.getSystemService(INPUT_METHOD_SERVICE);
        _inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void showKeyboardEdittext(Activity _activity, EditText _edittext) {
        _edittext.requestFocus();
        InputMethodManager _imm = (InputMethodManager) _activity.getSystemService(INPUT_METHOD_SERVICE);
        _imm.showSoftInput(_edittext, InputMethodManager.SHOW_IMPLICIT);
    }

}

package com.futureworkshops.android.autocapture.presentation.common;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.futureworkshops.android.autocapture.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Custom toggle button that defines the states available for the camera Flash control.
 * <p>
 * <p> Available states are mutually exclusive:
 * <ul>
 * <li>STATE_FLASH_AUTO</li>
 * <li>STATE_FLASH_ON</li>
 * <li>STATE_FLASH_OFF</li>
 * </ul>
 * </p>
 * <p>
 * <p> The button will handle toggling state automatically.</p>
 */
public class FlashToggleButton extends android.support.v7.widget.AppCompatButton implements View.OnClickListener {

    public static final int FLASH_TOGGLE_AUTO = 15;
    public static final int FLASH_TOGGLE_ON = 16;
    public static final int FLASH_TOGGLE_OFF = 17;

    @IntDef({
            FLASH_TOGGLE_AUTO, FLASH_TOGGLE_ON, FLASH_TOGGLE_OFF
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface FlashTogglState {
    }

    private static final int[] STATE_FLASH_AUTO = {R.attr.state_flash_auto};
    private static final int[] STATE_FLASH_ON = {R.attr.state_flash_on};
    private static final int[] STATE_FLASH_OFF = {R.attr.state_flash_off};


    @FlashTogglState
    private int mState = FLASH_TOGGLE_AUTO;

    private OnClickListener mClientClickListener;

    public FlashToggleButton(@NonNull Context context) {
        this(context, null);
    }

    public FlashToggleButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);

        // set the click listener to be this view, we call the super constructor because we override the local one
        super.setOnClickListener(this);
    }

    /**
     * In order to perform the toggle automatically we need to handle the click event
     * inside the FlashButton and then delegate to the user added listener.
     *
     * @param l
     */
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(this);

        mClientClickListener = l;
    }

    @Override
    public void onClick(View v) {
        toggleState();

        if (mClientClickListener != null) {
            mClientClickListener.onClick(v);
        }
    }

    @FlashTogglState
    public int getState() {
        return mState;
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 3);
        switch (mState) {
            case FLASH_TOGGLE_AUTO:
                mergeDrawableStates(drawableState, STATE_FLASH_AUTO);
                break;
            case FLASH_TOGGLE_ON:
                mergeDrawableStates(drawableState, STATE_FLASH_ON);
                break;
            case FLASH_TOGGLE_OFF:
                mergeDrawableStates(drawableState, STATE_FLASH_OFF);
                break;
        }

        return drawableState;
    }

    private void toggleState() {
        switch (mState) {
            case FLASH_TOGGLE_AUTO:
                mState = FLASH_TOGGLE_ON;
                break;
            case FLASH_TOGGLE_ON:
                mState = FLASH_TOGGLE_OFF;
                break;
            case FLASH_TOGGLE_OFF:
                mState = FLASH_TOGGLE_AUTO;
                break;
        }

        // go over the current state and update the flag we need
        final int[] currentState = getBackground().getState();
        final int[] newState = new int[currentState.length];

        // add the button states
        for (int i = 0; i < currentState.length; i++) {
            if (isFlashState(currentState[i])) {
                // add updated flash state
                newState[i] = getCurrentFlashStateAttr();
            } else {
                newState[i] = currentState[i];
            }
        }

        getBackground().setState(newState);
        getBackground().invalidateSelf();
    }

    private boolean isFlashState(int state) {
        return (state == R.attr.state_flash_auto)
                || (state == R.attr.state_flash_on)
                || (state == R.attr.state_flash_off);

    }

    /**
     * Return the attribute for the current button state.
     *
     * @return
     */
    private int getCurrentFlashStateAttr() {
        switch (mState) {
            case FLASH_TOGGLE_AUTO:
                return R.attr.state_flash_auto;
            case FLASH_TOGGLE_ON:
                return R.attr.state_flash_on;
            case FLASH_TOGGLE_OFF:
                return R.attr.state_flash_off;
            default:
                return R.attr.state_flash_off;
        }

    }
}

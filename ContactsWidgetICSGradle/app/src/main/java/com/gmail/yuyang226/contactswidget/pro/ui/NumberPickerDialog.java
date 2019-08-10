/**
 * 
 */
package com.gmail.yuyang226.contactswidget.pro.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.gmail.yuyang226.contactswidget.pro.R;

/**
 * @author yayu
 *
 */
public class NumberPickerDialog extends AlertDialog {
	private int minValue = 0;
	private int maxValue = Integer.MAX_VALUE;
	private int defaultValue = minValue;
	private OnNumberSetListener listener;
	private NumberPicker numberPicker;

	/**
	 * @param minValue
	 * @param maxValue
	 */
	public NumberPickerDialog(Context context, int titleResId, int minValue, int maxValue, int defaultValue, OnNumberSetListener listener) {
		super(context);
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.defaultValue = defaultValue;
		this.listener = listener;
		setTitle(titleResId);
		init();
	}
	
	private void init() {
		Context themeContext = getContext();
        setButton(BUTTON_POSITIVE, themeContext.getText(android.R.string.ok), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (numberPicker != null && listener != null) {
					listener.onNumberSet(numberPicker.getValue());
				}
			}
        	
        });
        setButton(BUTTON_NEGATIVE, themeContext.getText(android.R.string.cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				cancel();
			}
        });
        setIcon(0);

        LayoutInflater inflater =
                (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        View view = inflater.inflate(R.layout.dialog_number_picker, null);
        setView(view);
        numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(this.minValue);
        numberPicker.setMaxValue(this.maxValue);
        numberPicker.setValue(this.defaultValue);
	}
	
	public static interface OnNumberSetListener {
		public void onNumberSet(int number);
	}

}

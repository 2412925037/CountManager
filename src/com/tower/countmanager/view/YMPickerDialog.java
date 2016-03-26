package com.tower.countmanager.view;

import java.lang.reflect.Field;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;

public class YMPickerDialog extends DatePickerDialog {
	public YMPickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear) {
		super(context, callBack, year, monthOfYear, 3);
		this.setTitle(year + "年" + (monthOfYear + 1) + "月");
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		super.onDateChanged(view, year, month, day);
		this.setTitle(year + "年" + (month + 1) + "月");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.DatePickerDialog#show()
	 */
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
		DatePicker dp = findDatePicker((ViewGroup) this.getWindow().getDecorView());
		if (dp != null) {
			Class c = dp.getClass();
			Field f;
			try {
				f = c.getDeclaredField("mDayPicker");
				f.setAccessible(true);
				LinearLayout l = (LinearLayout) f.get(dp);
				l.setVisibility(View.GONE);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 从当前Dialog中查找DatePicker子控件
	 * 
	 * @param group
	 * @return
	 */
	public DatePicker findDatePicker(ViewGroup group) {
		if (group != null) {
			for (int i = 0, j = group.getChildCount(); i < j; i++) {
				View child = group.getChildAt(i);
				if (child instanceof DatePicker) {
					return (DatePicker) child;
				} else if (child instanceof ViewGroup) {
					DatePicker result = findDatePicker((ViewGroup) child);
					if (result != null)
						return result;
				}
			}
		}
		return null;

	}

}

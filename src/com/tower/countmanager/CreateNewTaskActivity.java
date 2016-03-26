package com.tower.countmanager;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CreateNewTaskActivity extends Activity {
	@ViewInject(R.id.subview_title)
	private TextView subviewTitle;
	@ViewInject(R.id.subview_title_arrow)
	ImageView titleLeftImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_task);
		ViewUtils.inject(this);
		subviewTitle.setText(R.string.create_new_task_title);
		titleLeftImg.setVisibility(View.VISIBLE);
	}

}

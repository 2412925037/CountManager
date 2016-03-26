/**
 * 
 */
package com.tower.countmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.bean.ChartInfoBean;
import com.tower.countmanager.bean.ChartMonthDataBean;
import com.tower.countmanager.bean.TaskInfoBean;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;

/**
 * 报表
 * 
 */
public class ReportFormActivity extends Activity {
	private static final String TAG = "ReportFormActivity";
	@ViewInject(R.id.lineChart)
	LineChartView mLineChartView;
	@ViewInject(R.id.comboLineColumnChartChart)
	private ComboLineColumnChartView chart;
	@ViewInject(R.id.columnChartView)
	ColumnChartView columnChartView;
	@ViewInject(R.id.subview_title)
	TextView subViewTitle;
	@ViewInject(R.id.subview_title_image)
	TextView subViewTitleRight;

	private ComboLineColumnChartData data;

	private int numberOfLines = 1;
	private int maxNumberOfLines = 4;
	private int numberOfPoints = 12;

	float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];

	private boolean hasAxes = true;
	private boolean hasAxesNames = true;
	private boolean hasPoints = true;
	private boolean hasLines = true;
	private boolean isCubic = true;
	private boolean hasLabels = true;
	private ProgressDialog dialog = null;
	List<AxisValue> mAxisValues = new ArrayList<AxisValue>();
	List<AxisValue> mAxisYValues = new ArrayList<AxisValue>();
	List<ChartMonthDataBean> mBeans;
	private boolean isLinePage = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_form);
		ViewUtils.inject(this);
		// initLineChart();
		loadProjectInfo();
		// generateValues();
		// generateData();
		subViewTitle.setText(R.string.report_form_score_statistics);
		subViewTitleRight.setText(R.string.report_form_task_complete_num);
		subViewTitleRight.setVisibility(View.VISIBLE);
	}

	private void createDialog() {
		if (dialog == null) {
			dialog = ProgressDialog.show(this, null, getString(R.string.app_connecting_dialog_text));
			dialog.setCancelable(false);
		} else
			dialog.show();
	}

	private void loadProjectInfo() {
		createDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		RequestServerUtils util = new RequestServerUtils();
		util.load2Server(map, Const.GET_REPORT_CHART_INFO_URL, handlerLoad, Utils.getToken(this));
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void initLineChart(List<ChartMonthDataBean> mBeans) {
        LineChartData data = generateLineData(mBeans);
        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName(getString(R.string.report_form_month));
                axisX.setValues(mAxisValues);
                axisY.setName(getString(R.string.report_form_score));
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        mLineChartView.setLineChartData(data);

    }
	
	private void initColumChart(List<ChartMonthDataBean> mBeans) {
		ColumnChartData data = generateColumnData(mBeans);

		 if (hasAxes) {
             Axis axisX = new Axis();
             Axis axisY = new Axis().setHasLines(true);
             if (hasAxesNames) {
                 axisX.setName(getString(R.string.report_form_month));
                 axisX.setValues(mAxisValues);
                 axisY.setName(getString(R.string.report_form_task_complete_num));
             }
             data.setAxisXBottom(axisX);
             data.setAxisYLeft(axisY);
         } else {
             data.setAxisXBottom(null);
             data.setAxisYLeft(null);
         }
        columnChartView.setColumnChartData(data);

    }

	private void generateValues(List<ChartMonthDataBean> mBeans) {
		for (int i = 0; i < maxNumberOfLines; ++i) {
			for (int j = 0; j < numberOfPoints; ++j) {
				// randomNumbersTab[i][j] = (float) Math.random() * 50f + 5;
				randomNumbersTab[i][j] = Float.parseFloat(mBeans.get(j).getScore());
			}
		}
	}
	
	  @OnClick(value = {R.id.subview_title_arrow,R.id.subview_title_image})
	    public void onButtonClick(View v){
	        switch(v.getId()) {
	            case R.id.subview_title_arrow :
	                finish();
	                break;
	            case R.id.subview_title_image :
	                if (isLinePage) {
	                	subViewTitleRight.setText(R.string.report_form_score);
	                	subViewTitle.setText(R.string.report_form_task_statistics);
	                	isLinePage = false;
	                	mLineChartView.setVisibility(View.GONE);
	                	columnChartView.setVisibility(View.VISIBLE);
	                	
					}else {
						subViewTitleRight.setText(R.string.report_form_task_complete_num);
						subViewTitle.setText(R.string.report_form_score_statistics);
	                	isLinePage = true;
	                	mLineChartView.setVisibility(View.VISIBLE);
	                	columnChartView.setVisibility(View.GONE);
					}
	                break;
	        }
	    }

	private void generateData(List<ChartMonthDataBean> mBeans) {
		// Chart looks the best when line data and column data have similar
		// maximum viewports.
		data = new ComboLineColumnChartData(generateColumnData(mBeans), generateLineData(mBeans));

		if (hasAxes) {
			// List<AxisValue> mAxisValues = new ArrayList<AxisValue>();
			// for (int i = 1; i <= 12 ; i++) {
			// mAxisValues.add(new AxisValue(i).setLabel(String.valueOf(i)));
			// //为每个对应的i设置相应的label(显示在X轴)
			// }
			Axis axisX = new Axis();
			Axis axisY = new Axis().setHasLines(true);
			if (hasAxesNames) {
				axisX.setName(getString(R.string.report_form_month));
				axisX.setValues(mAxisValues);
				axisY.setName(getString(R.string.report_form_task_complete_num));
			}

			data.setAxisXBottom(axisX);
			data.setAxisYLeft(axisY);
		} else {
			data.setAxisXBottom(null);
			data.setAxisYLeft(null);
		}
		// chart.setInteractive(true);
		chart.setValueSelectionEnabled(true);
		chart.setComboLineColumnChartData(data);
	}

	private LineChartData generateLineData(List<ChartMonthDataBean> mBeans) {

		List<Line> lines = new ArrayList<Line>();
		for (int i = 0; i < numberOfLines; ++i) {
			List<PointValue> values = new ArrayList<PointValue>();
			for (int j = 0; j < numberOfPoints; ++j) {
				values.add(new PointValue(j, randomNumbersTab[i][j]));
			}

			Line line = new Line(values);
			line.setColor(ChartUtils.COLORS[i]);
			// line.setCubic(isCubic);
			// line.setHasLabels(hasLabels);
			// line.setHasLines(hasLines);
			// line.setHasPoints(hasPoints);
			line.setCubic(isCubic);
			line.setHasLabels(hasLabels);
			// line.setHasLabelsOnlyForSelected(true);
			line.setHasLines(hasLines);
			line.setHasPoints(hasPoints);
			lines.add(line);
		}

		LineChartData lineChartData = new LineChartData(lines);
		return lineChartData;

	}

	private ColumnChartData generateColumnData(List<ChartMonthDataBean> mBeans) {
		int numSubcolumns = 1;
		// int numColumns = 12;
		int numColumns = mBeans.size();
		// Column can have many subcolumns, here by default I use 1 subcolumn in
		// each of 8 columns.
		List<Column> columns = new ArrayList<Column>();
		List<SubcolumnValue> values;
		for (int i = 0; i < numColumns; ++i) {
			values = new ArrayList<SubcolumnValue>();
			for (int j = 0; j < numSubcolumns; ++j) {
				float taskCount = Float.parseFloat(mBeans.get(i).getMonthsum());
				values.add(new SubcolumnValue(taskCount, ChartUtils.COLOR_GREEN));
			}
			Column column = new Column(values);
//			column.setHasLabelsOnlyForSelected(true);
			column.setHasLabels(true);
			columns.add(column);
		}

		ColumnChartData columnChartData = new ColumnChartData(columns);
		// columnChartData.setStacked(true);
		return columnChartData;
	}

	private Handler handlerLoad = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (dialog != null)
				dialog.dismiss();
			Log.e(TAG, (String) msg.obj);
			switch (msg.what) {
			case Const.REQUEST_SERVER_SUCCESS:
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String success = jsonObject.getString("success");
					if ("00".equals(success)) {
						Toast.makeText(ReportFormActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT)
								.show();
					} else if ("01".equals(success)) {
						Gson gson = new Gson();
						ChartInfoBean cBean = gson.fromJson(jsonObject.getString("result"), ChartInfoBean.class);
						mBeans = cBean.getMonthDate();
						for (int i = 0; i < mBeans.size(); i++) {
							ChartMonthDataBean mBean = mBeans.get(i);
							mAxisValues.add(new AxisValue(i).setLabel(mBean.getMonth())); // 为每个对应的i设置相应的label(显示在X轴)
						}
						numberOfPoints = mBeans.size();
						generateValues(mBeans);
//						generateData(mBeans);
						initLineChart(mBeans);
						initColumChart(mBeans);
						mLineChartView.setVisibility(View.VISIBLE);
					} else
						Utils.sessionTimeout(ReportFormActivity.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Const.REQUEST_SERVER_FAILURE:
				Toast.makeText(ReportFormActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
}

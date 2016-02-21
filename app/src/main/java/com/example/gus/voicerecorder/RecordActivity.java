package com.example.gus.voicerecorder;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class RecordActivity extends AppCompatActivity {

    private int counter = 0;
    public Chronometer chronometer;
    public MediaRecorder myAudioRecorder ;
    public boolean recording;
    public Timer timer;
//    public BarChart mChart;
//    ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
//    ArrayList<String > labels = new ArrayList<String>();
//    BarDataSet dataSet;
//    BarData data;
    private GraphicalView mChart;

    private XYSeries visitsSeries ;
    private XYMultipleSeriesDataset dataset;

    private XYSeriesRenderer visitsRenderer;
    private XYMultipleSeriesRenderer multiRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_record);

        chronometer = (Chronometer) findViewById(R.id.chrono);
//        mChart = (BarChart) findViewById(R.id.chart);
//        mChart.setDrawGridBackground(false);
//        mChart.setDescription("");
//        mChart.setVisibleYRangeMaximum((float) 32767, YAxis.AxisDependency.LEFT);
//
//
//         dataSet = new BarDataSet(entries,"DataSet");
//        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//        data = new BarData(labels,dataSet);
//
//        mChart.setData(data);
//        mChart.invalidate();

        // Setting up chart
        setupChart();



        myAudioRecorder = new MediaRecorder();

        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);


        FloatingActionButton mic = (FloatingActionButton) findViewById(R.id.record);
        mic.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
        mic.setRippleColor(getResources().getColor(R.color.colorAccent));

        final LinearLayout bottomBar = (LinearLayout) findViewById(R.id.bottomBar);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter == 0) {
                    EditDialog();
                    counter++;
                } else {
                    if (chronometer != null && myAudioRecorder != null) {
                        recording = false;
                       // timer.cancel();
                        chronometer.stop();
                        counter--;
                        myAudioRecorder.stop();
                        myAudioRecorder.release();
                    }
                    Intent intent = new Intent(RecordActivity.this, MainActivity.class);
                    intent.putExtra("act", "record");

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            // the context of the activity
                            RecordActivity.this,

                            // For each shared element, add to this method a new Pair item,
                            // which contains the reference of the view we are transitioning *from*,
                            // and the value of the transitionName attribute

                            new Pair<View, String>(v.findViewById(R.id.record),
                                    "fab"),
                            new Pair<View, String>(bottomBar,
                                    "bottom")
                    );

                    startActivity(intent, options.toBundle());
                }

            }
        });


    }
    private void setupChart(){

        // Creating an  XYSeries for Sound
        visitsSeries = new XYSeries("");

        // Creating a dataset to hold each series
        dataset = new XYMultipleSeriesDataset();
        // Adding Visits Series to the dataset
        dataset.addSeries(visitsSeries);

        // Creating XYSeriesRenderer to customize visitsSeries
        visitsRenderer = new XYSeriesRenderer();
        visitsRenderer.setColor(getResources().getColor(R.color.colorAccent));
        visitsRenderer.setPointStyle(PointStyle.CIRCLE);
        visitsRenderer.setFillPoints(true);
        visitsRenderer.setDisplayChartValues(false);
        visitsRenderer.setLineWidth(2);



        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        multiRenderer = new XYMultipleSeriesRenderer();


        multiRenderer.setMargins(new int[]{0, 0, 0, 0});
        multiRenderer.setPanEnabled(false);
        multiRenderer.setShowLegend(false);
        multiRenderer.setChartTitle("");
        multiRenderer.setFitLegend(true);

        //Eliminate the Shite
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setXTitle("");
        multiRenderer.setShowLabels(false);
        multiRenderer.setShowAxes(false);
        
        multiRenderer.setMarginsColor(Color.TRANSPARENT);
        multiRenderer.setBackgroundColor(Color.TRANSPARENT);
        multiRenderer.setXLabelsColor(Color.TRANSPARENT);
        multiRenderer.setYLabelsColor(0, Color.TRANSPARENT);
        multiRenderer.setGridColor(Color.TRANSPARENT);
        multiRenderer.setXLabelsPadding(0);
        multiRenderer.setYLabelsPadding(0);

        //Establish Beginning Dimensions
        multiRenderer.setXAxisMin(0);
        multiRenderer.setXAxisMax(10);
        multiRenderer.setYAxisMin(0);
        multiRenderer.setYAxisMax(0);


        //Make the bar graphs touching
        multiRenderer.setBarSpacing(0);

        // Adding visitsRenderer to multipleRenderer
        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
        // should be same
        multiRenderer.addSeriesRenderer(visitsRenderer);

        // Getting a reference to LinearLayout of the MainActivity Layout
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart);

        mChart = (GraphicalView) ChartFactory.getBarChartView(getBaseContext(), dataset, multiRenderer, org.achartengine.chart.BarChart.Type.DEFAULT);

        // Adding the Line Chart to the LinearLayout
        chartContainer.addView(mChart);
    }
    private class ChartTask extends AsyncTask<Void, String, Void> {

        // Generates dummy data in a non-ui thread
        @Override
        protected Void doInBackground(Void... params) {
            int i = 0,yMax=0;
            try{
                do{
                    String [] values = new String[2];

                    int maxAmp = myAudioRecorder.getMaxAmplitude();
                    if(maxAmp>yMax){
                        yMax =maxAmp;
                        multiRenderer.setYAxisMax(yMax);
                    }
                    if(i>10){
                        multiRenderer.setXAxisMin(multiRenderer.getXAxisMin()+1);
                        multiRenderer.setXAxisMax(multiRenderer.getXAxisMax()+1);
                    }

                    Log.d("record",Integer.toString(maxAmp));
                    values[0] = Integer.toString(i);
                    values[1] = Integer.toString(maxAmp);

                    publishProgress(values);
                    Thread.sleep(100);

                    i++;
                }while(recording);
            }catch(Exception e){ }
            return null;
        }

        // Plotting generated data in the graph
        @Override
        protected void onProgressUpdate(String... values) {
            visitsSeries.add(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
            Log.d("test",values[1]);
            mChart.repaint();
        }
    }

    public void recordingGraph() {

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

               // Log.d("Record Volume", Float.toString((float) maxAmp));

//                BarEntry bob = new BarEntry((float) (maxAmp / 10000)
//                        , (int) (SystemClock.elapsedRealtime() - chronometer.getBase()));
//               // BarEntry bob = new BarEntry((float) 1
//               //         , (int) (SystemClock.elapsedRealtime() - chronometer.getBase()));
//                //entries.add(bob);
//
//                dataSet.addEntry(bob);
//                data.addEntry(bob ,1);
//                labels.add(Long.toString(SystemClock.elapsedRealtime() - chronometer.getBase()));
//
//                mChart.notifyDataSetChanged();
//                mChart.moveViewToY(mChart.getData().getYValCount(), YAxis.AxisDependency.LEFT);
//                mChart.moveViewToX((mChart.getData().getXValCount()));
            }
        }, 0, 1000);//put here time 1000 milliseconds=1 second
    }


    public void EditDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View FileDialogView = factory.inflate(
                R.layout.filedialog, null);
        EditText myEditText = (EditText) FileDialogView.findViewById(R.id.filename);

        final AlertDialog FileDialog = new AlertDialog.Builder(this).create();
        FileDialog.setView(FileDialogView);
        FileDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        FileDialogView.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myAudioRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/VoiceRecorder/" + ((TextView) FileDialogView.findViewById(R.id.filename)).getText().toString() + ".mp3");
                try {
                    myAudioRecorder.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                myAudioRecorder.start();
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                recording=true;
                // Start plotting chart
                new ChartTask().execute();

                FileDialog.dismiss();
            }
        });
        FileDialogView.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FileDialog.dismiss();

            }
        });

        FileDialog.show();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(RecordActivity.this,MainActivity.class);
        setIntent.putExtra("act","record");
        startActivity(setIntent);
    }

}

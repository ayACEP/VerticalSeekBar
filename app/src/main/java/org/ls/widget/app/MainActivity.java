package org.ls.widget.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.widget.SeekBar;
import android.widget.TextView;

import org.ls.widget.VerticalSeekBar;

public class MainActivity extends Activity {

    private VerticalSeekBar seekBar1;
    private VerticalSeekBar seekBar2;
    private SeekBar seek;
    private AppCompatSeekBar seekAppcompat;
    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = (TextView) findViewById(R.id.txt);
        seekBar1 = (VerticalSeekBar) findViewById(R.id.seek_bar_1);
        seekBar2 = (VerticalSeekBar) findViewById(R.id.seek_bar_2);
        seek = (SeekBar) findViewById(R.id.seek);
        seekAppcompat = (AppCompatSeekBar) findViewById(R.id.seek_appcompat);

        seekBar1.setOnVerticalSeekBarChangeListener(new VerticalSeekBar.OnVerticalSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekbar) {
                txt.setText("VerticalSeekBar onStartTrackingTouch");
            }
            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekbar) {
                txt.setText("VerticalSeekBar onStopTrackingTouch");
            }
            @Override
            public void onProgressChanged(VerticalSeekBar seekbar, int progress, boolean fromUser) {
                txt.setText("VerticalSeekBar " + progress);
            }
        });
        seekBar2.setOnVerticalSeekBarChangeListener(new VerticalSeekBar.OnVerticalSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekbar) {
                txt.setText("Custom VerticalSeekBar onStartTrackingTouch");
            }
            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekbar) {
                txt.setText("Custom VerticalSeekBar onStopTrackingTouch");
            }
            @Override
            public void onProgressChanged(VerticalSeekBar seekbar, int progress, boolean fromUser) {
                txt.setText("Custom VerticalSeekBar " + progress);
            }
        });
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                txt.setText("Default SeekBar onStartTrackingTouch");
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txt.setText("Default SeekBar onStopTrackingTouch");
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txt.setText("Default SeekBar " + progress);
            }
        });
        seekAppcompat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                txt.setText("AppCompat SeekBar onStartTrackingTouch");
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txt.setText("AppCompat SeekBar onStopTrackingTouch");
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txt.setText("AppCompat SeekBar " + progress);
            }
        });
    }
}

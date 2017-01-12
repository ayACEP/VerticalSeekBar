package org.ls.widget.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import org.ls.widget.VerticalSeekBar;

public class MainActivity extends Activity {

    private VerticalSeekBar seekBar1;
    private VerticalSeekBar seekBar2;
    private SeekBar seek;
    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = (TextView) findViewById(R.id.txt);
        seek = (SeekBar) findViewById(R.id.seek);
        seekBar1 = (VerticalSeekBar) findViewById(R.id.seek_bar_1);
        seekBar2 = (VerticalSeekBar) findViewById(R.id.seek_bar_2);

//        seek.setEnabled(false);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int[] state = seekBar.getDrawableState();
                progress = 1;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int[] state = seekBar.getDrawableState();
                int progress = 1;
            }
        });
        seekBar1.setOnVerticalSeekBarChangeListener(new VerticalSeekBar.OnVerticalSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekbar) {
                txt.setText("seekBar1 onStartTrackingTouch");
            }
            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekbar) {
                txt.setText("seekBar1 onStopTrackingTouch");
            }
            @Override
            public void onProgressChanged(VerticalSeekBar seekbar, int progress, boolean fromUser) {
                txt.setText("seekBar1 " + progress);
            }
        });
        seekBar2.setOnVerticalSeekBarChangeListener(new VerticalSeekBar.OnVerticalSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekbar) {
                txt.setText("seekBar2 onStartTrackingTouch");
            }
            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekbar) {
                txt.setText("seekBar2 onStopTrackingTouch");
            }
            @Override
            public void onProgressChanged(VerticalSeekBar seekbar, int progress, boolean fromUser) {
                txt.setText("seekBar2 " + progress);
            }
        });
    }
}

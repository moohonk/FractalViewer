package com.example.charles.fractalviewer_v1;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Vector;

/*
 _________
|\   \    \
| \ __\____\   <----
|  |   |    |
|  |___|____|
 \ |   |    |
  \|___|____|










 */import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MainActivity extends ActionBarActivity {
    public int[] rule = {0, 0, 0, 0, 0, 0, 0};
    public int i;
    public int initialDepth = 3;
    public SeekBar sB1, sB2, sB3, sB4, sB5, sB6, sB7;
    public TextView debugger, depthField;
    public SeekBar sB;
    public SeekBar[] numbers;
    public double[][] ptOffset = new double[][]
    {
            { 0, -1, -1,  0,  0, -1, -1},
            {-1, -1,  0,  0, -1, -1,  0},
            { 0,  0,  0, -1, -1, -1, -1}
    };

    // Resource matrices to be stored in JME ---------------------------||
    //                                                                  ||
    public double[][] basePts = new double[][]  //                      ||
    {//      1  2  3  4  5  6   7   8   9  10  11  12  13  14           ||
            {2, 0, 0, 1, 1, 2,  2,  2,  0,  0,  0,  1,  1,  0}, // X    ||
            {2, 2, 1, 1, 0, 0,  0,  2,  2,  0,  0,  0,  1,  1}, // Y    ||
            {0, 0, 0, 0, 0, 0, -2, -2, -2, -2, -1, -1, -1, -1}  // Z    ||
    };
    public int[][] polygons = new int[][]
            {
                    { 1,  2,  9,  8},           //1
                    { 1,  6,  7,  8},           //2
                    { 7,  8,  9, 10},           //3
                    { 3,  4, 13, 14},           //4
                    { 4,  5, 12, 13},           //5
                    {11, 12, 13, 14},           //6
                    { 1,  2,  3,  4,  5,  6},   //7
                    { 2,  9, 10, 11, 14,  3},   //8
                    { 5,  6,  7, 10, 11, 12}    //9
            };
    //                                                                  ||
    //                                                                  ||
    //------------------------------------------------------------------||

    public double[][] multiplyMatices(double[][] m1, int r1, int c1, double[][] m2, int r2, int c2)
    {
        assert(c1 == r2);
        double[][] a = new double[r1][c2];
        for(int i=0;i<r1;i++)
        {
            for(int j=0;j<c2;j++)
            {
                a[i][j] = 0;
                for(int k=0;k<c1;k++)
                    a[i][j] += m1[i][k]*m2[k][j];
            }
        }
        return a;
    }


    int sinZ(int zDir){return (zDir % 2) * (1-2*(zDir/2));}
    int cosZ(int zDir){return sinZ((zDir + 1) % 4);}
    int sinX(int xDir){return -1 * xDir;}
    int cosX(int xDir){return sinX(xDir) + 1;}

    public double[][] getRotMat(int dir)
    {
        int x = dir / 4;
        int z = dir % 4;
        double cosz = cosZ(z);
        double sinz = sinZ(z);
        double cosx = cosX(x);
        double sinx = sinX(x);
        return new double[][]{
          {cosz, -cosx*sinz,  sinx*sinz},
          {sinz,  cosx*cosz, -sinx*cosz},
          {   0,       sinx,       cosx}};
    }
    public void sendDataToJME(double[] point, int dir, double len){

    }
    public void recurse(double[] point, int dir, double len, int depth){
    // Recursion
        double l = len/2;
        if(depth > 1)
        {
            //double l = len/2;
            double[][] rotMat = getRotMat(dir);
            double[][] newOffsets = multiplyMatices(ptOffset, 7, 3, rotMat, 3, 3);
            for(i = 0; i < 7; i++)
                for (int j = 0; j < 3; j++)
                    newOffsets[i][j] *= l;

            for(i = 0; i < 7; i++)
            {
                int oldX = (dir / 4);
                int oldZ = (dir % 4);
                int newX = (oldX + (rule[i] / 4)) % 2;
                int newZ = (oldZ + (rule[i] % 4)) % 4;
                int newDir = newZ + 4*newX;
                double[] newPoint = new double[3];
                for(int j = 0; j < 3; j++)
                    newPoint[j] = point[j] + newOffsets[i][j];
                recurse(newPoint, newDir, l, depth-1);
            }
        }
    //Base Case
        // Send point, length (L), and direction to JME
        if(depth == 1) sendDataToJME(point, dir, len);
    }
    public void fractalGen(View v){

        double[] point = {1.0, 1.0, 1.0};
        double len     = 1.0;
        int dir        = 0;
        //int depth      = 3;
        recurse(point, dir, len, initialDepth);
    }
    public void changeDepth(View v){


    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sB1 = (SeekBar) findViewById(R.id.sB1);
        sB2 = (SeekBar) findViewById(R.id.sB2);
        sB3 = (SeekBar) findViewById(R.id.sB3);
        sB4 = (SeekBar) findViewById(R.id.sB4);
        sB5 = (SeekBar) findViewById(R.id.sB5);
        sB6 = (SeekBar) findViewById(R.id.sB6);
        sB7 = (SeekBar) findViewById(R.id.sB7);
        debugger = (TextView) findViewById(R.id.debug);
        sB = (SeekBar)findViewById(R.id.theDepth);

        depthField = (TextView) findViewById(R.id.depthField);
        SeekBar[] numbers1 = {sB1, sB2, sB3, sB4, sB5, sB6, sB7};
        numbers = numbers1;
        for(i = 0; i < 6; i++){
            numbers[i].setMax(7);
            numbers[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){@Override public void onStopTrackingTouch(SeekBar arg0){
            }
                @Override public void onStartTrackingTouch(SeekBar arg0){
                }
                @Override public void onProgressChanged(SeekBar arg0,int arg1,boolean arg2){
                    if(arg2) {
//                        debugger.setText("Changed: ".concat(Integer.toString(arg1+1)));
//                        depthField.setText("Depth: ".concat(Integer.toString(arg1+1)));
                        rule[i] = arg1;
                    }
                }});
        }
        SeekBar.OnSeekBarChangeListener listener=new SeekBar.OnSeekBarChangeListener(){
            @Override public void onStopTrackingTouch(SeekBar arg0){
            }
            @Override public void onStartTrackingTouch(SeekBar arg0){
            }
            @Override public void onProgressChanged(SeekBar arg0,int arg1,boolean arg2){
                if(arg2) {
                    debugger.setText("Changed: ".concat(Integer.toString(arg1+1)));
                    depthField.setText("Depth: ".concat(Integer.toString(arg1+1)));
                    initialDepth = arg1+1;
                }
            }
        };
        sB.setOnSeekBarChangeListener(listener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

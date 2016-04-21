/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Kyriakos Alexandrou (Kiki)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.heartratemeasurement;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.kyriakosalexandrou.bluetoothtoolkit.ui.BaseGraph;

public class HrmGraph extends BaseGraph {
    // graph line
    private XYSeries mCurrentSeries;

    public HrmGraph(Context context, View view) {
        super(context, view);
        initChart();
        setStartingPositions(mRenderer.getXAxisMin(), mRenderer.getXAxisMax(),
                mRenderer.getYAxisMin(), mRenderer.getYAxisMax());
        paintChart();
    }

    @Override
    protected void initChart() {
        super.initChart();
        mCurrentSeries = new XYSeries("BPM");
        mRenderer.setYTitle("Heartbeat");
        mRenderer.setPanEnabled(IS_GRAPH_MOVABLE_BY_TOUCH_HORIZONTAL,
                IS_GRAPH_MOVABLE_BY_TOUCH_VERTICAL);
        mRenderer.setZoomEnabled(IS_GRAPH_ZOOMABLE_BY_TOUCH_HORIZONTAL,
                IS_GRAPH_ZOOMABLE_BY_TOUCH_VERTICAL);
        mRenderer.setYAxisMax(210);
        mRenderer.setYAxisMin(0);
        mDataset.addSeries(mCurrentSeries);
        // graph line
        XYSeriesRenderer mCurrentRenderer = new XYSeriesRenderer();
        mCurrentRenderer.setColor(Color.parseColor("#FF0000")); // FF0000
        mCurrentRenderer.setLineWidth(LINE_WEIGHT);
        mRenderer.addSeriesRenderer(mCurrentRenderer);
    }

    public void addNewData(double bpm) {
        // x, y
        mCurrentSeries.add(calculateElapsedTime(), bpm);
        paintChart();
    }

    @Override
    public void clearGraph() {
        mCurrentSeries.clear();
        setAxisStartingPoints();
        paintChart();
    }
}
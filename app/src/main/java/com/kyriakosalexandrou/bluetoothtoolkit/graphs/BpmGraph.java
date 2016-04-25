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

package com.kyriakosalexandrou.bluetoothtoolkit.graphs;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

public class BpmGraph extends BaseGraph {
    // graph lines
    private XYSeries mSystolicLineXYSeries, mDystolicLineXYSeries,
            mArterialPressureLineXYSeries;
    private XYSeriesRenderer mSystolicLineXYSeriesRenderer,
            mDystolicLineXYSeriesRenderer,
            mArterialPressureLineXYSeriesRenderer;

    public BpmGraph(Context context, View view) {
        super(context, view);
        initChart();
        setStartingPositions(mRenderer.getXAxisMin(), mRenderer.getXAxisMax(),
                mRenderer.getYAxisMin(), mRenderer.getYAxisMax());
        paintChart();
    }

    @Override
    protected void initChart() {
        super.initChart();
        mSystolicLineXYSeries = new XYSeries("Systolic");
        mDataset.addSeries(mSystolicLineXYSeries);

        mDystolicLineXYSeries = new XYSeries("Dystolic");
        mDataset.addSeries(mDystolicLineXYSeries);

        mArterialPressureLineXYSeries = new XYSeries("Arterial Pressure");
        mDataset.addSeries(mArterialPressureLineXYSeries);

        mRenderer.setYTitle("Blood Pressure");
        mRenderer.setYAxisMax(210);
        mRenderer.setYAxisMin(0);
        mRenderer.setXAxisMax(1.1);
        mRenderer.setXAxisMin(0);
        mRenderer.setPanEnabled(IS_GRAPH_MOVABLE_BY_TOUCH_HORIZONTAL,
                IS_GRAPH_MOVABLE_BY_TOUCH_VERTICAL);
        mRenderer.setZoomEnabled(IS_GRAPH_ZOOMABLE_BY_TOUCH_HORIZONTAL,
                IS_GRAPH_ZOOMABLE_BY_TOUCH_VERTICAL);

        // line styling
        setSystolicLineStyle();
        setDystolicLineStyle();
        setArterialPressureLineStyle();

        mRenderer.addSeriesRenderer(mSystolicLineXYSeriesRenderer);
        mRenderer.addSeriesRenderer(mDystolicLineXYSeriesRenderer);
        mRenderer.addSeriesRenderer(mArterialPressureLineXYSeriesRenderer);
    }

    public void addNewData(double systolic, double dystolic,
                           double arterialPressure) {
        // x, y
        mSystolicLineXYSeries.add(calculateElapsedTime(), systolic);
        mDystolicLineXYSeries.add(calculateElapsedTime(), dystolic);
        mArterialPressureLineXYSeries.add(calculateElapsedTime(),
                arterialPressure);
        paintChart();
    }

    @Override
    public void clearGraph() {
        mSystolicLineXYSeries.clear();
        mDystolicLineXYSeries.clear();
        mArterialPressureLineXYSeries.clear();
        setAxisStartingPoints();
        paintChart();
    }

    private void setSystolicLineStyle() {
        mSystolicLineXYSeriesRenderer = new XYSeriesRenderer();
        mSystolicLineXYSeriesRenderer.setColor(Color.parseColor("#FF0000"));
        mSystolicLineXYSeriesRenderer.setLineWidth(LINE_WEIGHT);
    }

    private void setDystolicLineStyle() {
        mDystolicLineXYSeriesRenderer = new XYSeriesRenderer();
        mDystolicLineXYSeriesRenderer.setColor(Color.parseColor("#0034C5"));
        mDystolicLineXYSeriesRenderer.setLineWidth(LINE_WEIGHT);
    }

    private void setArterialPressureLineStyle() {
        mArterialPressureLineXYSeriesRenderer = new XYSeriesRenderer();
        mArterialPressureLineXYSeriesRenderer.setColor(Color
                .parseColor("#008000"));
        mArterialPressureLineXYSeriesRenderer.setLineWidth(LINE_WEIGHT);
    }
}
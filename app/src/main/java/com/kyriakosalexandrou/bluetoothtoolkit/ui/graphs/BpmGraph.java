package com.kyriakosalexandrou.bluetoothtoolkit.ui.graphs;

import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.kyriakosalexandrou.bluetoothtoolkit.ui.BaseGraph;

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
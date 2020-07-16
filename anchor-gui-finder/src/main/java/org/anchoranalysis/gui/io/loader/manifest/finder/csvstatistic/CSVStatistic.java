/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic;

import org.anchoranalysis.core.index.IIndexGetter;

public class CSVStatistic implements IIndexGetter, Comparable<IIndexGetter> {

    private boolean hasAccptProb = false;
    private boolean hasAccptProbAll = false;
    private boolean hasAccptProbRand = false;
    private boolean hasKernelProp = false;
    private boolean hasKernelAccpt = false;
    private boolean hasTime = false;
    private boolean hasTimePerIter = false;
    private boolean hasIntervalTimePerIter = false;
    private boolean hasTemperature = false;

    private int iter;
    private double size;
    private double nrg;
    private double accptProb;
    private double accptProbAll;
    private double accptProbRand;
    private double[] kernelProp;
    private double[] kernelAccpt;
    private double time;
    private double timePerIter;
    private double intervalTimePerIter;
    private double temperature;

    public CSVStatistic duplicate() {
        CSVStatistic out = new CSVStatistic();
        out.hasAccptProb = hasAccptProb;
        out.hasAccptProbAll = hasAccptProbAll;
        out.hasAccptProbRand = hasAccptProbRand;
        out.hasKernelProp = hasKernelProp;
        out.hasKernelAccpt = hasKernelAccpt;
        out.hasTime = hasTime;
        out.hasTimePerIter = hasTimePerIter;
        out.hasIntervalTimePerIter = hasIntervalTimePerIter;
        out.hasTemperature = hasTemperature;
        out.iter = iter;
        out.size = size;
        out.nrg = nrg;
        out.accptProb = accptProb;
        out.accptProbAll = accptProbAll;
        out.accptProbRand = accptProbRand;
        out.kernelProp = kernelProp;
        out.kernelAccpt = kernelAccpt;
        out.time = time;
        out.timePerIter = timePerIter;
        out.intervalTimePerIter = intervalTimePerIter;
        out.temperature = temperature;
        return out;
    }

    public boolean hasAccptProb() {
        return hasAccptProb;
    }

    public boolean hasAccptProbAll() {
        return hasAccptProbAll;
    }

    public boolean hasAccptProbRand() {
        return hasAccptProbRand;
    }

    public int getIter() {
        return iter;
    }

    public void setIter(int iter) {
        this.iter = iter;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public double getNrg() {
        return nrg;
    }

    public void setNrg(double nrg) {
        this.nrg = nrg;
    }

    @Override
    public int getIndex() {
        return getIter();
    }

    @Override
    public int compareTo(IIndexGetter arg0) {
        return Integer.valueOf(iter).compareTo(arg0.getIndex());
    }

    public double getAccptProb() {
        return accptProb;
    }

    public void setAccptProb(double accptProb) {
        this.accptProb = accptProb;
        this.hasAccptProb = true;
    }

    public double getAccptProbAll() {
        return accptProbAll;
    }

    public void setAccptProbAll(double accptProbAll) {
        this.accptProbAll = accptProbAll;
        this.hasAccptProbAll = true;
    }

    public double getAccptProbRand() {
        return accptProbRand;
    }

    public void setAccptProbRand(double accptProbRand) {
        this.accptProbRand = accptProbRand;
        this.hasAccptProbRand = true;
    }

    public double[] getKernelProp() {
        return kernelProp;
    }

    public void setKernelProp(double[] kernelProp) {
        this.kernelProp = kernelProp;
        this.hasKernelProp = true;
    }

    public double[] getKernelAccpt() {
        return kernelAccpt;
    }

    public void setKernelAccpt(double[] kernelAccpt) {
        this.kernelAccpt = kernelAccpt;
        this.hasKernelAccpt = true;
    }

    public boolean isHasKernelProp() {
        return hasKernelProp;
    }

    public boolean isHasKernelAccpt() {
        return hasKernelAccpt;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
        this.hasTime = true;
    }

    public double getTimePerIter() {
        return timePerIter;
    }

    public void setTimePerIter(double timePerIter) {
        this.timePerIter = timePerIter;
        this.hasTimePerIter = true;
    }

    public double getIntervalTimePerIter() {
        return intervalTimePerIter;
    }

    public void setIntervalTimePerIter(double intervalTimePerIter) {
        this.intervalTimePerIter = intervalTimePerIter;
        this.hasIntervalTimePerIter = true;
    }

    public boolean hasTime() {
        return hasTime;
    }

    public boolean hasTimePerIter() {
        return hasTimePerIter;
    }

    public boolean hasIntervalTimePerIter() {
        return hasIntervalTimePerIter;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
        this.hasTemperature = true;
    }

    public boolean hasTemperature() {
        return hasTemperature;
    }
}

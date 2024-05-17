package com.alex.demo.count;

/**
 * @Author: Stephen
 * @Date: 2020/3/4 11:42
 * @Content:
 */
public class Agg {
    private float sum=0;
    private float avg=0;
    private float count=0;

    public float getSum() {
        return sum;
    }

    public void setSum(float sum) {
        this.sum = sum;
    }

    public float getAvg() {
        return avg;
    }

    public void setAvg(float avg) {
        this.avg = avg;
    }

    public float getCount() {
        return count;
    }

    public void setCount(float count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Agg{" +
                "sum=" + sum +
                ", avg=" + avg +
                ", count=" + count +
                '}';
    }
}



package com.stn.redis.beans;

import java.util.List;

public class ActionMap {

    private List<CounterBean> retrieve;

    private List<CounterBean> save;

    public List<CounterBean> getRetrieve() {
        return retrieve;
    }

    public List<CounterBean> getSave() {
        return save;
    }

    public void setRetrieve(List<CounterBean> retrieve) {
        this.retrieve = retrieve;
    }

    public void setSave(List<CounterBean> save) {
        this.save = save;
    }

    public ActionMap(List<CounterBean> retrieve, List<CounterBean> save) {
        this.retrieve = retrieve;
        this.save = save;
    }

    @Override
    public String toString() {
        return "ActionSpec{" +
                "retrieve=" + retrieve +
                ", save=" + save +
                '}';
    }
}

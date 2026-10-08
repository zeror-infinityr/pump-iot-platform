package cn.sx.sxupr.simulator.device;

import cn.sx.sxupr.simulator.config.SimulatorProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class SamplingIntervalState {

    private final AtomicLong intervalMs;

    public SamplingIntervalState(
            SimulatorProperties properties) {

        this.intervalMs =
                new AtomicLong(
                        properties.intervalMs()
                );
    }

    public long getIntervalMs() {
        return intervalMs.get();
    }

    public void setIntervalSeconds(
            long seconds) {

        intervalMs.set(
                seconds * 1000
        );
    }
}
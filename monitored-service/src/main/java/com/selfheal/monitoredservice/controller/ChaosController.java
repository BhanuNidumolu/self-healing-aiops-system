package com.selfheal.monitoredservice.controller;

import com.selfheal.monitoredservice.model.ChaosState;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicBoolean;

@RestController
public class ChaosController {

    private final ChaosState state;

    public ChaosController(ChaosState state) {
        this.state = state;
    }

    @GetMapping("/chaos/cpu")
    public String cpuStress() {
        state.getCpuStress().set(true);

        new Thread(() -> {
            long end = System.currentTimeMillis() + 20000;
            while (System.currentTimeMillis() < end && state.getCpuStress().get()) {
                Math.pow(Math.random(), Math.random());
            }
            state.getCpuStress().set(false);
        }).start();

        return "🔥 CPU Stress triggered for 20s!";
    }

    @GetMapping("/chaos/cpu/stop")
    public String stopCpu() {
        state.getCpuStress().set(false);
        return "🛑 CPU stress stopped";
    }


    @GetMapping("/chaos/memory")
    public String memoryLeak() {
        state.getMemoryLeakHolder().add(new byte[50 * 1024 * 1024]);
        state.setMemoryLoad(state.getMemoryLoad() + 50);
        return "💣 Added 50MB memory chunk";
    }

    @GetMapping("/chaos/slow")
    public String slow() {
        state.setSlowFactor(state.getSlowFactor() + 300);
        return "🐢 Slow response mode increased";
    }

    @GetMapping("/chaos/reset")
    public String reset() {
        state.getMemoryLeakHolder().clear();
        state.setMemoryLoad(0);
        state.getCpuStress().set(false);
        state.setSlowFactor(0);
        return "♻ All chaos restored back to stable state";
    }
}

package com.selfheal.monitoredservice.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Getter @Setter
public class ChaosState {

    private final List<byte[]> memoryLeakHolder = new ArrayList<>();
    private int memoryLoad = 0;                     // affects memory + CPU
    private AtomicBoolean cpuStress = new AtomicBoolean(false);
    private int slowFactor = 0;
}

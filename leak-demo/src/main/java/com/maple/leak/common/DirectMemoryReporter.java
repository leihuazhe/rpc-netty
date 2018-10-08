package com.maple.leak.common;

import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author maple 2018.09.10 下午4:46
 */
public class DirectMemoryReporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectMemoryReporter.class);
    private static final int _1K = 1024;
    private static final String BUSINESS_KEY = "netty_direct_memory";

    private AtomicLong directMemory;

    private DataUnit dataUnit;

    public void setDataUnit(DataUnit dataUnit) {
        this.dataUnit = dataUnit;
    }

    private DirectMemoryReporter() {
        init();
    }

    private static DirectMemoryReporter directMemoryReporter;

    public static DirectMemoryReporter getInstance() {
        if (directMemoryReporter == null) {
            synchronized (DirectMemoryReporter.class) {
                if (directMemoryReporter == null) {
                    directMemoryReporter = new DirectMemoryReporter();
                }
            }
        }
        return directMemoryReporter;
    }

    private void init() {
        Field field = ReflectionUtils.findField(PlatformDependent.class, "DIRECT_MEMORY_COUNTER");
        field.setAccessible(true);

        try {
            directMemory = (AtomicLong) field.get(PlatformDependent.class);
        } catch (IllegalAccessException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void startReport() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::doReport, 0, 3, TimeUnit.SECONDS);
    }

    private void doReport() {
        if (dataUnit == DataUnit.BYTE) {
            int memoryInkb = (int) (directMemory.get());
            LOGGER.info("{}: {} byte", BUSINESS_KEY, memoryInkb);
        } else {
            int memoryInkb = (int) (directMemory.get() / _1K);
            LOGGER.info("{}: {} kb", BUSINESS_KEY, memoryInkb);
        }
    }

    public enum DataUnit {
        BYTE,
        KILO_BYTE
    }
}

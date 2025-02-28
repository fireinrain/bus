/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org OSHI and other contributors.                 *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 ********************************************************************************/
package org.aoju.bus.health.windows.drivers;

import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.core.lang.tuple.Pair;
import org.aoju.bus.health.windows.PerfCounterQuery;
import org.aoju.bus.health.windows.PerfCounterWildcardQuery;
import org.aoju.bus.health.windows.PerfCounterWildcardQuery.PdhCounterWildcardProperty;

import java.util.List;
import java.util.Map;

/**
 * Utility to query Process Information performance counter
 *
 * @author Kimi Liu
 * @version 6.1.1
 * @since JDK 1.8+
 */
@ThreadSafe
public final class ProcessInformation {

    private static final String WIN32_PROCESS = "Win32_Process";
    private static final String PROCESS = "Process";
    private static final String WIN32_PROCESS_WHERE_NOT_NAME_LIKE_TOTAL = "Win32_Process WHERE NOT Name LIKE\"%_Total\"";

    private ProcessInformation() {
    }

    /**
     * Returns process counters.
     *
     * @return Process counters for each process.
     */
    public static Pair<List<String>, Map<ProcessPerformanceProperty, List<Long>>> queryProcessCounters() {
        return PerfCounterWildcardQuery.queryInstancesAndValues(ProcessPerformanceProperty.class, PROCESS,
                WIN32_PROCESS_WHERE_NOT_NAME_LIKE_TOTAL);
    }

    /**
     * Returns handle counters
     *
     * @return Process handle counters for each process.
     */
    public static Pair<List<String>, Map<HandleCountProperty, List<Long>>> queryHandles() {
        return PerfCounterWildcardQuery.queryInstancesAndValues(HandleCountProperty.class, PROCESS, WIN32_PROCESS);
    }

    /**
     * Process performance counters
     */
    public enum ProcessPerformanceProperty implements PdhCounterWildcardProperty {
        // First element defines WMI instance name field and PDH instance filter
        NAME(PerfCounterQuery.NOT_TOTAL_INSTANCES),
        // Remaining elements define counters
        PRIORITY("Priority Base"),
        CREATIONDATE("Elapsed Time"),
        PROCESSID("ID Process"),
        PARENTPROCESSID("Creating Process ID"),
        READTRANSFERCOUNT("IO Read Bytes/sec"),
        WRITETRANSFERCOUNT("IO Write Bytes/sec"),
        PRIVATEPAGECOUNT("Working Set - Private"),
        PAGEFAULTSPERSEC("Page Faults/sec");

        private final String counter;

        ProcessPerformanceProperty(String counter) {
            this.counter = counter;
        }

        @Override
        public String getCounter() {
            return counter;
        }
    }

    /**
     * Handle performance counters
     */
    public enum HandleCountProperty implements PdhCounterWildcardProperty {
        // First element defines WMI instance name field and PDH instance filter
        NAME(PerfCounterQuery.TOTAL_INSTANCE),
        // Remaining elements define counters
        HANDLECOUNT("Handle Count");

        private final String counter;

        HandleCountProperty(String counter) {
            this.counter = counter;
        }

        @Override
        public String getCounter() {
            return counter;
        }
    }

}

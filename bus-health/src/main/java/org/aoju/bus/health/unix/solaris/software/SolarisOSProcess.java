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
package org.aoju.bus.health.unix.solaris.software;

import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.core.lang.RegEx;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.health.Builder;
import org.aoju.bus.health.Executor;
import org.aoju.bus.health.Memoize;
import org.aoju.bus.health.builtin.software.AbstractOSProcess;
import org.aoju.bus.health.builtin.software.OSProcess;
import org.aoju.bus.health.builtin.software.OSThread;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Kimi Liu
 * @version 6.1.1
 * @since JDK 1.8+
 */
@ThreadSafe
public class SolarisOSProcess extends AbstractOSProcess {

    private Supplier<Integer> bitness = Memoize.memoize(this::queryBitness);

    private String name;
    private String path = "";
    private String commandLine;
    private String user;
    private String userID;
    private String group;
    private String groupID;
    private State state = State.INVALID;
    private int parentProcessID;
    private int threadCount;
    private int priority;
    private long virtualSize;
    private long residentSetSize;
    private long kernelTime;
    private long userTime;
    private long startTime;
    private long upTime;
    private long bytesRead;
    private long bytesWritten;

    public SolarisOSProcess(int pid, String[] split) {
        super(pid);
        updateAttributes(split);
    }

    /**
     * Merges results of a ps and prstat query, since Solaris thread details are not
     * available in a single command. Package private to permit access by
     * SolarisOSThread.
     *
     * @param psThreadInfo     output from ps command.
     * @param prstatThreadInfo output from the prstat command.
     * @return a map with key as thread id and an array of command outputs as value
     */
    static Map<Integer, String[]> parseAndMergeThreadInfo(List<String> psThreadInfo, List<String> prstatThreadInfo) {
        Map<Integer, String[]> map = new HashMap<>();
        final String[] mergedSplit = new String[9];
        // 0-lwpid, 1-state,2-elapsedtime,3-kerneltime, 4-usertime, 5-address,
        // 6-priority
        if (psThreadInfo.size() > 1) { // first row is header
            psThreadInfo.stream().skip(1).forEach(threadInfo -> {
                String[] psSplit = RegEx.SPACES.split(threadInfo.trim());
                if (psSplit.length == 7) {
                    // copying the 1st 7 results from ps command output
                    for (int idx = 0; idx < psSplit.length; idx++) {
                        if (idx == 0) { // index 0 has threadid
                            map.put(Builder.parseIntOrDefault(psSplit[idx], 0), mergedSplit);
                        }
                        mergedSplit[idx] = psSplit[idx];
                    }
                }
            });
            // 0-pid, 1-username, 2-usertime, 3-sys, 4-trp, 5-tfl, 6-dfl, 7-lck, 8-slp,
            // 9-lat, 10-vcx, 11-icx, 12-scl, 13-sig, 14-process/lwpid
            if (prstatThreadInfo.size() > 1) { // first row is header
                prstatThreadInfo.stream().skip(1).forEach(threadInfo -> {
                    String[] splitPrstat = RegEx.SPACES.split(threadInfo.trim());
                    if (splitPrstat.length == 15) {
                        int idxAfterForwardSlash = splitPrstat[14].lastIndexOf(Symbol.C_SLASH) + 1; // format is process/lwpid
                        if (idxAfterForwardSlash > 0 && idxAfterForwardSlash < splitPrstat[14].length()) {
                            String threadId = splitPrstat[14].substring(idxAfterForwardSlash); // getting the thread id
                            String[] existingSplit = map.get(Integer.parseInt(threadId));
                            if (existingSplit != null) { // if thread wasn't in ps command output
                                existingSplit[7] = splitPrstat[10]; // voluntary context switch
                                existingSplit[8] = splitPrstat[11]; // involuntary context switch
                            }
                        }
                    }
                });
            }
        }
        return map;
    }

    /***
     * Returns Enum STATE for the state value obtained from status string of
     * thread/process.
     *
     * @param stateValue
     *            state value from the status string
     * @return The state
     */
    static State getStateFromOutput(char stateValue) {
        State state;
        switch (stateValue) {
            case 'O':
                state = OSProcess.State.RUNNING;
                break;
            case 'S':
                state = OSProcess.State.SLEEPING;
                break;
            case 'R':
            case 'W':
                state = OSProcess.State.WAITING;
                break;
            case 'Z':
                state = OSProcess.State.ZOMBIE;
                break;
            case 'T':
                state = OSProcess.State.STOPPED;
                break;
            default:
                state = OSProcess.State.OTHER;
                break;
        }
        return state;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getCommandLine() {
        return this.commandLine;
    }

    @Override
    public String getCurrentWorkingDirectory() {
        return Builder.getCwd(getProcessID());
    }

    @Override
    public String getUser() {
        return this.user;
    }

    @Override
    public String getUserID() {
        return this.userID;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public String getGroupID() {
        return this.groupID;
    }

    @Override
    public State getState() {
        return this.state;
    }

    @Override
    public int getParentProcessID() {
        return this.parentProcessID;
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public long getVirtualSize() {
        return this.virtualSize;
    }

    @Override
    public long getResidentSetSize() {
        return this.residentSetSize;
    }

    @Override
    public long getKernelTime() {
        return this.kernelTime;
    }

    @Override
    public long getUserTime() {
        return this.userTime;
    }

    @Override
    public long getUpTime() {
        return this.upTime;
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public long getBytesRead() {
        return this.bytesRead;
    }

    @Override
    public long getBytesWritten() {
        return this.bytesWritten;
    }

    @Override
    public long getOpenFiles() {
        return Builder.getOpenFiles(getProcessID());
    }

    @Override
    public int getBitness() {
        return this.bitness.get();
    }

    private int queryBitness() {
        List<String> pflags = Executor.runNative("pflags " + getProcessID());
        for (String line : pflags) {
            if (line.contains("data model")) {
                if (line.contains("LP32")) {
                    return 32;
                } else if (line.contains("LP64")) {
                    return 64;
                }
            }
        }
        return 0;
    }

    @Override
    public long getAffinityMask() {
        long bitMask = 0L;
        String cpuset = Executor.getFirstAnswer("pbind -q " + getProcessID());
        // Sample output:
        // <empty string if no binding>
        // pid 101048 strongly bound to processor(s) 0 1 2 3.
        if (cpuset.isEmpty()) {
            List<String> allProcs = Executor.runNative("psrinfo");
            for (String proc : allProcs) {
                String[] split = RegEx.SPACES.split(proc);
                int bitToSet = Builder.parseIntOrDefault(split[0], -1);
                if (bitToSet >= 0) {
                    bitMask |= 1L << bitToSet;
                }
            }
            return bitMask;
        } else if (cpuset.endsWith(Symbol.DOT) && cpuset.contains("strongly bound to processor(s)")) {
            String parse = cpuset.substring(0, cpuset.length() - 1);
            String[] split = RegEx.SPACES.split(parse);
            for (int i = split.length - 1; i >= 0; i--) {
                int bitToSet = Builder.parseIntOrDefault(split[i], -1);
                if (bitToSet >= 0) {
                    bitMask |= 1L << bitToSet;
                } else {
                    // Once we run into the word processor(s) we're done
                    break;
                }
            }
        }
        return bitMask;
    }

    @Override
    public List<OSThread> getThreadDetails() {
        List<String> threadListInfo1 = Executor
                .runNative("ps -o lwp,s,etime,stime,time,addr,pri -p " + getProcessID());
        List<String> threadListInfo2 = Executor.runNative("prstat -L -v -p " + getProcessID());
        Map<Integer, String[]> threadMap = parseAndMergeThreadInfo(threadListInfo1, threadListInfo2);
        if (threadMap.keySet().size() > 1) {
            return threadMap.entrySet().stream().map(entry -> new SolarisOSThread(getProcessID(), entry.getValue()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean updateAttributes() {
        List<String> procList = Executor.runNative(
                "ps -o s,pid,ppid,user,uid,group,gid,nlwp,pri,vsz,rss,etime,time,comm,args -p " + getProcessID());
        if (procList.size() > 1) {
            String[] split = RegEx.SPACES.split(procList.get(1).trim(), 15);
            // Elements should match ps command order
            if (split.length == 15) {
                return updateAttributes(split);
            }
        }
        this.state = State.INVALID;
        return false;
    }

    private boolean updateAttributes(String[] split) {
        long now = System.currentTimeMillis();
        this.state = getStateFromOutput(split[0].charAt(0));
        this.parentProcessID = Builder.parseIntOrDefault(split[2], 0);
        this.user = split[3];
        this.userID = split[4];
        this.group = split[5];
        this.groupID = split[6];
        this.threadCount = Builder.parseIntOrDefault(split[7], 0);
        this.priority = Builder.parseIntOrDefault(split[8], 0);
        // These are in KB, multiply
        this.virtualSize = Builder.parseLongOrDefault(split[9], 0) * 1024;
        this.residentSetSize = Builder.parseLongOrDefault(split[10], 0) * 1024;
        // Avoid divide by zero for processes up less than a second
        long elapsedTime = Builder.parseDHMSOrDefault(split[11], 0L);
        this.upTime = elapsedTime < 1L ? 1L : elapsedTime;
        this.startTime = now - this.upTime;
        this.kernelTime = 0L;
        this.userTime = Builder.parseDHMSOrDefault(split[12], 0L);
        this.path = split[13];
        this.name = this.path.substring(this.path.lastIndexOf(Symbol.C_SLASH) + 1);
        this.commandLine = split[14];

        return true;
    }

}

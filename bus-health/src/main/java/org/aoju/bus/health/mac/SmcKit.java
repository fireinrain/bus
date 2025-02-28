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
package org.aoju.bus.health.mac;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.mac.IOKit.IOConnect;
import com.sun.jna.platform.mac.IOKit.IOService;
import com.sun.jna.platform.mac.IOKitUtil;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;
import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.health.Builder;
import org.aoju.bus.logger.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提供对Mac OS上SMC调用的访问
 *
 * @author Kimi Liu
 * @version 6.1.1
 * @since JDK 1.8+
 */
@ThreadSafe
public final class SmcKit {

    public static final String SMC_KEY_FAN_NUM = "FNum";
    public static final String SMC_KEY_FAN_SPEED = "F%dAc";
    public static final String SMC_KEY_CPU_TEMP = "TC0P";
    public static final String SMC_KEY_CPU_VOLTAGE = "VC0C";
    public static final byte SMC_CMD_READ_BYTES = 5;
    public static final byte SMC_CMD_READ_KEYINFO = 9;
    public static final int KERNEL_INDEX_SMC = 2;
    private static final IOKit IO = IOKit.INSTANCE;
    /**
     * 用于匹配返回类型的字节数组
     */
    private static final byte[] DATATYPE_SP78 = Builder.asciiStringToByteArray("sp78", 5);
    private static final byte[] DATATYPE_FPE2 = Builder.asciiStringToByteArray("fpe2", 5);
    private static final byte[] DATATYPE_FLT = Builder.asciiStringToByteArray("flt ", 5);
    /**
     * 映射缓存信息，由后续调用所需的键检索
     */
    private static Map<Integer, SMCKeyDataKeyInfo> keyInfoCache = new ConcurrentHashMap<>();

    private SmcKit() {
    }

    /**
     * 打开到SMC的连接.
     *
     * @return 如果连接成功，则为空
     */
    public static IOConnect smcOpen() {
        IOService smcService = IOKitUtil.getMatchingService("AppleSMC");
        if (smcService != null) {
            PointerByReference connPtr = new PointerByReference();
            int result = IO.IOServiceOpen(smcService, SystemB.INSTANCE.mach_task_self(), 0, connPtr);
            smcService.release();
            if (result == 0) {
                return new IOConnect(connPtr.getValue());
            } else if (Logger.get().isError()) {
                Logger.error(String.format("Unable to open connection to AppleSMC service. Error: 0x%08x", result));
            }
        } else {
            Logger.error("Unable to locate AppleSMC service");
        }
        return null;
    }

    /**
     * 与SMC紧密连接
     *
     * @param conn 连接
     * @return 0表示成功，非0表示失败
     */
    public static int smcClose(IOConnect conn) {
        return IO.IOServiceClose(conn);
    }

    /**
     * 从SMC中获取浮点数据类型(SP78, FPE2, FLT)的值
     *
     * @param conn 连接
     * @param key  检索键
     * @return 双精度值
     */
    public static double smcGetFloat(IOConnect conn, String key) {
        SMCVal val = new SMCVal();
        int result = smcReadKey(conn, key, val);
        if (result == 0 && val.dataSize > 0) {
            if (Arrays.equals(val.dataType, DATATYPE_SP78) && val.dataSize == 2) {
                // 第一个位是符号，接下来的7位是整数部分，最后的8位是小数部分
                return val.bytes[0] + val.bytes[1] / 256d;
            } else if (Arrays.equals(val.dataType, DATATYPE_FPE2) && val.dataSize == 2) {
                // 前E(14)位是整数部分，后2位是小数部分
                return Builder.byteArrayToFloat(val.bytes, val.dataSize, 2);
            } else if (Arrays.equals(val.dataType, DATATYPE_FLT) && val.dataSize == 4) {
                // 标准的32位浮点数
                return ByteBuffer.wrap(val.bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            }
        }
        return 0d;
    }

    /**
     * 从SMC中获取一个64位的整数值
     *
     * @param conn 连接
     * @param key  检索键
     * @return 长整型值
     */
    public static long smcGetLong(IOConnect conn, String key) {
        SMCVal val = new SMCVal();
        int result = smcReadKey(conn, key, val);
        if (result == 0) {
            return Builder.byteArrayToLong(val.bytes, val.dataSize);
        }
        return 0;
    }

    /**
     * 获取缓存的keyInfo(如果存在的话)，或者生成新的keyInfo
     *
     * @param conn            连接
     * @param inputStructure  关键数据输入
     * @param outputStructure 关键数据输出
     * @return 0表示成功，非0表示失败
     */
    public static int smcGetKeyInfo(IOConnect conn, SMCKeyData inputStructure, SMCKeyData outputStructure) {
        if (keyInfoCache.containsKey(inputStructure.key)) {
            SMCKeyDataKeyInfo keyInfo = keyInfoCache.get(inputStructure.key);
            outputStructure.keyInfo.dataSize = keyInfo.dataSize;
            outputStructure.keyInfo.dataType = keyInfo.dataType;
            outputStructure.keyInfo.dataAttributes = keyInfo.dataAttributes;
        } else {
            inputStructure.data8 = SMC_CMD_READ_KEYINFO;
            int result = smcCall(conn, KERNEL_INDEX_SMC, inputStructure, outputStructure);
            if (result != 0) {
                return result;
            }
            SMCKeyDataKeyInfo keyInfo = new SMCKeyDataKeyInfo();
            keyInfo.dataSize = outputStructure.keyInfo.dataSize;
            keyInfo.dataType = outputStructure.keyInfo.dataType;
            keyInfo.dataAttributes = outputStructure.keyInfo.dataAttributes;
            keyInfoCache.put(inputStructure.key, keyInfo);
        }
        return 0;
    }

    /**
     * Read a key from SMC
     *
     * @param conn 连接
     * @param key  读取key
     * @param val  接收结果
     * @return 0表示成功，非0表示失败
     */
    public static int smcReadKey(IOConnect conn, String key, SMCVal val) {
        SMCKeyData inputStructure = new SMCKeyData();
        SMCKeyData outputStructure = new SMCKeyData();

        inputStructure.key = (int) Builder.strToLong(key, 4);
        int result = smcGetKeyInfo(conn, inputStructure, outputStructure);
        if (result == 0) {
            val.dataSize = outputStructure.keyInfo.dataSize;
            val.dataType = Builder.longToByteArray(outputStructure.keyInfo.dataType, 4, 5);

            inputStructure.keyInfo.dataSize = val.dataSize;
            inputStructure.data8 = SMC_CMD_READ_BYTES;

            result = smcCall(conn, KERNEL_INDEX_SMC, inputStructure, outputStructure);
            if (result == 0) {
                System.arraycopy(outputStructure.bytes, 0, val.bytes, 0, val.bytes.length);
                return 0;
            }
        }
        return result;
    }

    /**
     * 调用SMC
     *
     * @param conn            连接
     * @param index           内核指数
     * @param inputStructure  关键数据输入
     * @param outputStructure 关键数据输出
     * @return 0表示成功，非0表示失败
     */
    public static int smcCall(IOConnect conn, int index, SMCKeyData inputStructure, SMCKeyData outputStructure) {
        return IO.IOConnectCallStructMethod(conn, index, inputStructure, new NativeLong(inputStructure.size()),
                outputStructure, new NativeLongByReference(new NativeLong(outputStructure.size())));
    }

    /**
     * 保存SMC版本查询的返回值.
     */
    @FieldOrder({"major", "minor", "build", "reserved", "release"})
    public static class SMCKeyDataVers extends Structure {
        public byte major;
        public byte minor;
        public byte build;
        public byte reserved;
        public short release;

    }

    /**
     * 保存SMC pLimit查询的返回值
     */
    @FieldOrder({"version", "length", "cpuPLimit", "gpuPLimit", "memPLimit"})
    public static class SMCKeyDataPLimitData extends Structure {
        public short version;
        public short length;
        public int cpuPLimit;
        public int gpuPLimit;
        public int memPLimit;
    }

    /**
     * 保存SMC KeyInfo查询的返回值
     */
    @FieldOrder({"dataSize", "dataType", "dataAttributes"})
    public static class SMCKeyDataKeyInfo extends Structure {
        public int dataSize;
        public int dataType;
        public byte dataAttributes;
    }

    /**
     * 保存SMC查询的返回值
     */
    @FieldOrder({"key", "vers", "pLimitData", "keyInfo", "result", "status", "data8", "data32", "bytes"})
    public static class SMCKeyData extends Structure {
        public int key;
        public SMCKeyDataVers vers;
        public SMCKeyDataPLimitData pLimitData;
        public SMCKeyDataKeyInfo keyInfo;
        public byte result;
        public byte status;
        public byte data8;
        public int data32;
        public byte[] bytes = new byte[32];
    }

    /**
     * 保持SMC值
     */
    @FieldOrder({"key", "dataSize", "dataType", "bytes"})
    public static class SMCVal extends Structure {
        public byte[] key = new byte[5];
        public int dataSize;
        public byte[] dataType = new byte[5];
        public byte[] bytes = new byte[32];
    }

}

/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
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
package org.aoju.bus.core.convert;

import org.aoju.bus.core.toolkit.BooleanKit;
import org.aoju.bus.core.toolkit.DateKit;
import org.aoju.bus.core.toolkit.MathKit;
import org.aoju.bus.core.toolkit.StringKit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 数字转换器
 * 支持类型为：
 * <ul>
 * <li><code>java.lang.Byte</code></li>
 * <li><code>java.lang.Short</code></li>
 * <li><code>java.lang.Integer</code></li>
 * <li><code>java.lang.Long</code></li>
 * <li><code>java.lang.Float</code></li>
 * <li><code>java.lang.Double</code></li>
 * <li><code>java.math.BigDecimal</code></li>
 * <li><code>java.math.BigInteger</code></li>
 * </ul>
 *
 * @author Kimi Liu
 * @version 6.1.1
 * @since JDK 1.8+
 */
public class NumberConverter extends AbstractConverter<Number> {

    private final Class<? extends Number> targetType;

    public NumberConverter() {
        this.targetType = Number.class;
    }

    /**
     * 构造
     *
     * @param clazz 需要转换的数字类型,默认 {@link Number}
     */
    public NumberConverter(Class<? extends Number> clazz) {
        this.targetType = (null == clazz) ? Number.class : clazz;
    }

    @Override
    protected Number convertInternal(Object value) {
        return convertInternal(value, this.targetType);
    }

    private Number convertInternal(Object value, Class<?> targetType) {
        if (Byte.class == targetType) {
            if (value instanceof Number) {
                return ((Number) value).byteValue();
            } else if (value instanceof Boolean) {
                return BooleanKit.toByteObj((Boolean) value);
            }
            final String valueStr = convertString(value);
            return StringKit.isBlank(valueStr) ? null : Byte.valueOf(valueStr);

        } else if (Short.class == targetType) {
            if (value instanceof Number) {
                return ((Number) value).shortValue();
            } else if (value instanceof Boolean) {
                return BooleanKit.toShortObj((Boolean) value);
            }
            final String valueStr = convertString(value);
            return StringKit.isBlank(valueStr) ? null : Short.valueOf(valueStr);

        } else if (Integer.class == targetType) {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else if (value instanceof Boolean) {
                return BooleanKit.toInteger((Boolean) value);
            } else if (value instanceof Date) {
                return (int) ((Date) value).getTime();
            } else if (value instanceof Calendar) {
                return (int) ((Calendar) value).getTimeInMillis();
            } else if (value instanceof TemporalAccessor) {
                return (int) DateKit.toInstant((TemporalAccessor) value).toEpochMilli();
            }
            final String valueStr = convertString(value);
            return StringKit.isBlank(valueStr) ? null : MathKit.parseInt(valueStr);

        } else if (AtomicInteger.class == targetType) {
            final Number number = convertInternal(value, Integer.class);
            if (null != number) {
                final AtomicInteger intValue = new AtomicInteger();
                intValue.set(number.intValue());
                return intValue;
            }
            return null;
        } else if (Long.class == targetType) {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            } else if (value instanceof Boolean) {
                return BooleanKit.toLongObj((Boolean) value);
            } else if (value instanceof Date) {
                return ((Date) value).getTime();
            } else if (value instanceof Calendar) {
                return ((Calendar) value).getTimeInMillis();
            } else if (value instanceof TemporalAccessor) {
                return DateKit.toInstant((TemporalAccessor) value).toEpochMilli();
            }
            final String valueStr = convertString(value);
            return StringKit.isBlank(valueStr) ? null : MathKit.parseLong(valueStr);

        } else if (AtomicLong.class == targetType) {
            final Number number = convertInternal(value, Long.class);
            if (null != number) {
                final AtomicLong longValue = new AtomicLong();
                longValue.set(number.longValue());
                return longValue;
            }
            return null;
        } else if (Float.class == targetType) {
            if (value instanceof Number) {
                return ((Number) value).floatValue();
            } else if (value instanceof Boolean) {
                return BooleanKit.toFloatObj((Boolean) value);
            }
            final String valueStr = convertString(value);
            return StringKit.isBlank(valueStr) ? null : Float.valueOf(valueStr);

        } else if (Double.class == targetType) {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else if (value instanceof Boolean) {
                return BooleanKit.toDoubleObj((Boolean) value);
            }
            final String valueStr = convertString(value);
            return StringKit.isBlank(valueStr) ? null : Double.valueOf(valueStr);

        } else if (BigDecimal.class == targetType) {
            return toBigDecimal(value);

        } else if (BigInteger.class == targetType) {
            return toBigInteger(value);

        } else if (Number.class == targetType) {
            if (value instanceof Number) {
                return (Number) value;
            } else if (value instanceof Boolean) {
                return BooleanKit.toInteger((Boolean) value);
            }
            final String valueStr = convertString(value);
            return StringKit.isBlank(valueStr) ? null : MathKit.parseNumber(valueStr);
        }

        throw new UnsupportedOperationException(StringKit.format("Unsupport Number type: {}", this.targetType.getName()));
    }

    /**
     * 转换为BigDecimal
     * 如果给定的值为空,或者转换失败,返回默认值
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    private BigDecimal toBigDecimal(Object value) {
        if (value instanceof Long) {
            return new BigDecimal((Long) value);
        } else if (value instanceof Integer) {
            return new BigDecimal((Integer) value);
        } else if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        } else if (value instanceof Boolean) {
            return new BigDecimal((boolean) value ? 1 : 0);
        }

        //对于Double类型,先要转换为String,避免精度问题
        final String valueStr = convertString(value);
        if (StringKit.isBlank(valueStr)) {
            return null;
        }
        return new BigDecimal(valueStr);
    }

    /**
     * 转换为BigInteger
     * 如果给定的值为空,或者转换失败,返回默认值
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    private BigInteger toBigInteger(Object value) {
        if (value instanceof Long) {
            return BigInteger.valueOf((Long) value);
        } else if (value instanceof Boolean) {
            return BigInteger.valueOf((boolean) value ? 1 : 0);
        }
        final String valueStr = convertString(value);
        if (StringKit.isBlank(valueStr)) {
            return null;
        }
        return new BigInteger(valueStr);
    }

    @Override
    protected String convertString(Object value) {
        String result = StringKit.trim(super.convertString(value));
        if (StringKit.isNotEmpty(result)) {
            final char c = Character.toUpperCase(result.charAt(result.length() - 1));
            if (c == 'D' || c == 'L' || c == 'F') {
                // 类型标识形式（例如123.6D）
                return StringKit.subPre(result, -1);
            }
        }
        return result;
    }

    @Override
    public Class<Number> getTargetType() {
        return (Class<Number>) this.targetType;
    }

}

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
package org.aoju.bus.core.date;

import org.aoju.bus.core.date.format.DateParser;
import org.aoju.bus.core.date.format.DatePrinter;
import org.aoju.bus.core.date.format.FormatBuilder;
import org.aoju.bus.core.lang.Assert;
import org.aoju.bus.core.lang.Fields;
import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.core.toolkit.DateKit;
import org.aoju.bus.core.toolkit.ObjectKit;
import org.aoju.bus.core.toolkit.StringKit;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 包装java.utils.Date
 *
 * @author Kimi Liu
 * @version 6.1.1
 * @since JDK 1.8+
 */
public class DateTime extends Date {

    private static final long serialVersionUID = 1L;

    /**
     * 是否可变对象
     */
    private boolean mutable = true;
    /**
     * 一周的第一天，默认是周一， 在设置或获得 WEEK_OF_MONTH 或 WEEK_OF_YEAR 字段时，Calendar 必须确定一个月或一年的第一个星期，以此作为参考点。
     */
    private Fields.Week firstDayOfWeek = Fields.Week.MONDAY;
    /**
     * 时区
     */
    private TimeZone timeZone;

    /**
     * 当前时间
     */
    public DateTime() {
        this(TimeZone.getDefault());
    }

    /**
     * 当前时间
     *
     * @param timeZone 时区
     */
    public DateTime(TimeZone timeZone) {
        this(System.currentTimeMillis(), timeZone);
    }

    /**
     * 给定日期的构造
     *
     * @param date 日期
     */
    public DateTime(Date date) {
        this(
                date.getTime(),
                (date instanceof DateTime) ? ((DateTime) date).timeZone : TimeZone.getDefault()
        );
    }

    /**
     * 给定日期的构造
     *
     * @param date     日期
     * @param timeZone 时区
     */
    public DateTime(Date date, TimeZone timeZone) {
        this(date.getTime(), timeZone);
    }

    /**
     * 给定日期的构造
     *
     * @param calendar {@link Calendar}
     */
    public DateTime(Calendar calendar) {
        this(calendar.getTime(), calendar.getTimeZone());
        this.setFirstDayOfWeek(Fields.Week.of(calendar.getFirstDayOfWeek()));
    }

    /**
     * 给定日期Instant的构造
     *
     * @param instant {@link Instant} 对象
     */
    public DateTime(Instant instant) {
        this(instant.toEpochMilli());
    }

    /**
     * 给定日期Instant的构造
     *
     * @param instant {@link Instant} 对象
     * @param zoneId  时区ID
     */
    public DateTime(Instant instant, ZoneId zoneId) {
        this(instant.toEpochMilli(), TimeZone.getTimeZone(ObjectKit.defaultIfNull(zoneId, ZoneId.systemDefault())));
    }

    /**
     * 给定日期TemporalAccessor的构造
     *
     * @param temporalAccessor {@link TemporalAccessor} 对象
     */
    public DateTime(TemporalAccessor temporalAccessor) {
        this(DateKit.toInstant(temporalAccessor));
    }

    /**
     * 给定日期ZonedDateTime的构造
     *
     * @param zonedDateTime {@link ZonedDateTime} 对象
     */
    public DateTime(ZonedDateTime zonedDateTime) {
        this(zonedDateTime.toInstant(), zonedDateTime.getZone());
    }

    /**
     * 给定日期毫秒数的构造
     *
     * @param timeMillis 日期毫秒数
     */
    public DateTime(long timeMillis) {
        this(timeMillis, TimeZone.getDefault());
    }

    /**
     * 给定日期毫秒数的构造
     *
     * @param timeMillis 日期毫秒数
     * @param timeZone   时区
     */
    public DateTime(long timeMillis, TimeZone timeZone) {
        super(timeMillis);
        this.timeZone = ObjectKit.defaultIfNull(timeZone, TimeZone.getDefault());
    }

    /**
     * 构造
     *
     * @param dateStr Date字符串
     * @param format  格式
     */
    public DateTime(CharSequence dateStr, String format) {
        this(dateStr, new SimpleDateFormat(format));
    }

    /**
     * 构造
     *
     * @param dateStr    Date字符串
     * @param dateFormat 格式化器 {@link SimpleDateFormat}
     */
    public DateTime(CharSequence dateStr, DateFormat dateFormat) {
        this(parse(dateStr, dateFormat), dateFormat.getTimeZone());
    }

    /**
     * 构建DateTime对象
     *
     * @param dateStr   Date字符串
     * @param formatter 格式化器,{@link DateTimeFormatter}
     */
    public DateTime(CharSequence dateStr, DateTimeFormatter formatter) {
        this(Instant.from(formatter.parse(dateStr)), formatter.getZone());
    }

    /**
     * 构造
     *
     * @param dateStr    Date字符串
     * @param dateParser 格式化器 {@link DateParser}，可以使用 {@link FormatBuilder}
     */
    public DateTime(CharSequence dateStr, DateParser dateParser) {
        this(parse(dateStr, dateParser), dateParser.getTimeZone());
    }

    /**
     * 转换时间戳为 DateTime
     *
     * @param timeMillis 时间戳，毫秒数
     * @return DateTime
     */
    public static DateTime of(long timeMillis) {
        return new DateTime(timeMillis);
    }

    /**
     * 转换JDK date为 DateTime
     *
     * @param date JDK Date
     * @return DateTime
     */
    public static DateTime of(Date date) {
        if (date instanceof DateTime) {
            return (DateTime) date;
        }
        return new DateTime(date);
    }

    /**
     * 转换 {@link Calendar} 为 DateTime
     *
     * @param calendar {@link Calendar}
     * @return DateTime
     */
    public static DateTime of(Calendar calendar) {
        return new DateTime(calendar);
    }

    /**
     * 构造
     *
     * @param dateStr Date字符串
     * @param format  格式
     * @return {@link DateTime}
     */
    public static DateTime of(String dateStr, String format) {
        return new DateTime(dateStr, format);
    }

    /**
     * 现在的时间
     *
     * @return 现在的时间
     */
    public static DateTime now() {
        return new DateTime();
    }

    /**
     * 转换字符串为Date
     *
     * @param dateStr    日期字符串
     * @param dateFormat {@link SimpleDateFormat}
     * @return {@link Date}
     */
    private static Date parse(CharSequence dateStr, DateFormat dateFormat) {
        Assert.notBlank(dateStr, "Date String must be not blank !");
        try {
            return dateFormat.parse(dateStr.toString());
        } catch (Exception e) {
            String pattern;
            if (dateFormat instanceof SimpleDateFormat) {
                pattern = ((SimpleDateFormat) dateFormat).toPattern();
            } else {
                pattern = dateFormat.toString();
            }
            throw new InstrumentException(StringKit.format("Parse [{}] with format [{}] error!", dateStr, pattern), e);
        }
    }

    /**
     * 转换字符串为Date
     *
     * @param dateStr 日期字符串
     * @param parser  {@link FormatBuilder}
     * @return {@link Date}
     */
    private static Date parse(CharSequence dateStr, DateParser parser) {
        Assert.notNull(parser, "Parser or DateFromat must be not null !");
        Assert.notBlank(dateStr, "Date String must be not blank !");
        try {
            return parser.parse(dateStr.toString());
        } catch (Exception e) {
            throw new InstrumentException("Parse [{}] with format [{}] error!", dateStr, parser.getPattern(), e);
        }
    }

    /**
     * 调整日期和时间
     * 如果此对象为可变对象，返回自身，否则返回新对象，设置是否可变对象见{@link #setMutable(boolean)}
     *
     * @param datePart 调整的部分 {@link Fields.DateField}
     * @param offset   偏移量，正数为向后偏移，负数为向前偏移
     * @return 如果此对象为可变对象，返回自身，否则返回新对象
     */
    public DateTime offset(Fields.DateField datePart, int offset) {
        if (Fields.DateField.ERA == datePart) {
            throw new IllegalArgumentException("ERA is not support offset!");
        }

        final Calendar cal = toCalendar();
        cal.add(datePart.getValue(), offset);

        DateTime dt = mutable ? this : ObjectKit.clone(this);
        return dt.setTimeInternal(cal.getTimeInMillis());
    }

    /**
     * 调整日期和时间
     * 返回调整后的新{@link DateTime}，不影响原对象
     *
     * @param datePart 调整的部分 {@link Fields.DateField}
     * @param offset   偏移量，正数为向后偏移，负数为向前偏移
     * @return 如果此对象为可变对象，返回自身，否则返回新对象
     */
    public DateTime offsetNew(Fields.DateField datePart, int offset) {
        final Calendar cal = toCalendar();
        cal.add(datePart.getValue(), offset);

        DateTime dt = ObjectKit.clone(this);
        return dt.setTimeInternal(cal.getTimeInMillis());
    }

    /**
     * 获得日期的某个部分
     * 例如获得年的部分,则使用 getField(Calendar.YEAR)
     *
     * @param field 表示日期的哪个部分的枚举 {@link Fields.DateField}
     * @return 某个部分的值
     */
    public int getField(Fields.DateField field) {
        return getField(field.getValue());
    }

    /**
     * 获得日期的某个部分
     * 例如获得年的部分，则使用 getField(Calendar.YEAR)
     *
     * @param field 表示日期的哪个部分的int值 {@link Calendar}
     * @return 某个部分的值
     */
    public int getField(int field) {
        return toCalendar().get(field);
    }

    /**
     * 设置日期的某个部分
     * 如果此对象为可变对象，返回自身，否则返回新对象，设置是否可变对象见{@link #setMutable(boolean)}
     *
     * @param field 表示日期的哪个部分的枚举 {@link Fields.DateField}
     * @param value 值
     * @return {@link DateTime}
     */
    public DateTime setField(Fields.DateField field, int value) {
        return setField(field.getValue(), value);
    }

    /**
     * 设置日期的某个部分
     * 如果此对象为可变对象，返回自身，否则返回新对象，设置是否可变对象见{@link #setMutable(boolean)}
     *
     * @param field 表示日期的哪个部分的int值 {@link Calendar}
     * @param value 值
     * @return {@link DateTime}
     */
    public DateTime setField(int field, int value) {
        final Calendar calendar = toCalendar();
        calendar.set(field, value);

        DateTime dt = this;
        if (false == mutable) {
            dt = ObjectKit.clone(this);
        }
        return dt.setTimeInternal(calendar.getTimeInMillis());
    }

    @Override
    public void setTime(long time) {
        if (mutable) {
            super.setTime(time);
        } else {
            throw new InstrumentException("This is not a mutable object !");
        }
    }

    /**
     * 获得年的部分
     *
     * @return 年的部分
     */
    public int year() {
        return getField(Fields.DateField.YEAR);
    }

    /**
     * 获得当前日期所属季度,从1开始计数
     *
     * @return 第几个季度 {@link Fields.Quarter}
     */
    public int quarter() {
        return month() / 3 + 1;
    }

    /**
     * 获得当前日期所属季度
     *
     * @return 第几个季度 {@link Fields.Quarter}
     */
    public Fields.Quarter quarterEnum() {
        return Fields.Quarter.of(quarter());
    }

    /**
     * 获得月份,从0开始计数
     *
     * @return 月份
     */
    public int month() {
        return getField(Fields.DateField.MONTH);
    }

    /**
     * 获得月份，从1开始计数
     * 由于{@link Calendar} 中的月份按照0开始计数,导致某些需求容易误解,因此如果想用1表示一月,2表示二月则调用此方法
     *
     * @return 月份
     */
    public int monthStartFromOne() {
        return month() + 1;
    }

    /**
     * 获得月份
     *
     * @return {@link Fields.Month}
     */
    public Fields.Month monthEnum() {
        return Fields.Month.of(month());
    }

    /**
     * 获得指定日期是所在年份的第几周
     * 此方法返回值与一周的第一天有关,比如：
     * 2016年1月3日为周日,如果一周的第一天为周日,那这天是第二周(返回2)
     * 如果一周的第一天为周一,那这天是第一周(返回1)
     *
     * @return 周
     */
    public int weekOfYear() {
        return getField(Fields.DateField.WEEK_OF_YEAR);
    }

    /**
     * 获得指定日期是所在月份的第几周
     * 此方法返回值与一周的第一天有关,比如：
     * 2016年1月3日为周日,如果一周的第一天为周日,那这天是第二周(返回2)
     * 如果一周的第一天为周一,那这天是第一周(返回1)
     *
     * @return 周
     */
    public int weekOfMonth() {
        return getField(Fields.DateField.WEEK_OF_MONTH);
    }

    /**
     * 获得指定日期是这个日期所在月份的第几天
     *
     * @return 天
     */
    public int dayOfMonth() {
        return getField(Fields.DateField.DAY_OF_MONTH);
    }

    /**
     * 获得指定日期是这个日期所在年份的第几天
     *
     * @return 天
     */
    public int dayOfYear() {
        return getField(Fields.DateField.DAY_OF_YEAR);
    }

    /**
     * 获得指定日期是星期几,1表示周日,2表示周一
     *
     * @return 星期几
     */
    public int dayOfWeek() {
        return getField(Fields.DateField.DAY_OF_WEEK);
    }

    /**
     * 获得天所在的周是这个月的第几周
     *
     * @return 天
     */
    public int dayOfWeekInMonth() {
        return getField(Fields.DateField.DAY_OF_WEEK_IN_MONTH);
    }

    /**
     * 获得指定日期是星期几
     *
     * @return {@link Fields.Week}
     */
    public Fields.Week dayOfWeekEnum() {
        return Fields.Week.of(dayOfWeek());
    }

    /**
     * 获得指定日期的小时数部分
     *
     * @param is24HourClock 是否24小时制
     * @return 小时数
     */
    public int hour(boolean is24HourClock) {
        return getField(is24HourClock ? Fields.DateField.HOUR_OF_DAY : Fields.DateField.HOUR);
    }

    /**
     * 获得指定日期的分钟数部分
     * 例如：10:04:15.250 = 4
     *
     * @return 分钟数
     */
    public int minute() {
        return getField(Fields.DateField.MINUTE);
    }

    /**
     * 获得指定日期的秒数部分
     *
     * @return 秒数
     */
    public int second() {
        return getField(Fields.DateField.SECOND);
    }

    /**
     * 获得指定日期的毫秒数部分
     *
     * @return 毫秒数
     */
    public int millsecond() {
        return getField(Fields.DateField.MILLISECOND);
    }

    /**
     * 是否为上午
     *
     * @return 是否为上午
     */
    public boolean isAM() {
        return Calendar.AM == getField(Fields.DateField.AM_PM);
    }

    /**
     * 是否为下午
     *
     * @return 是否为下午
     */
    public boolean isPM() {
        return Calendar.PM == getField(Fields.DateField.AM_PM);
    }

    /**
     * 是否为周末，周末指周六或者周日
     *
     * @return 是否为周末，周末指周六或者周日
     */
    public boolean isWeekend() {
        final int dayOfWeek = dayOfWeek();
        return Calendar.SATURDAY == dayOfWeek || Calendar.SUNDAY == dayOfWeek;
    }

    /**
     * 是否闰年
     *
     * @return 是否闰年
     * @see DateKit#isLeapYear(int)
     */
    public boolean isLeapYear() {
        return new DateKit().isLeapYear(year());
    }

    /**
     * 转换为Calendar, 默认 {@link Locale}
     *
     * @return {@link Calendar}
     */
    public Calendar toCalendar() {
        return toCalendar(Locale.getDefault(Locale.Category.FORMAT));
    }

    /**
     * 转换为Calendar
     *
     * @param locale 地域 {@link Locale}
     * @return {@link Calendar}
     */
    public Calendar toCalendar(Locale locale) {
        return toCalendar(this.timeZone, locale);
    }

    /**
     * 转换为Calendar
     *
     * @param zone 时区 {@link TimeZone}
     * @return {@link Calendar}
     */
    public Calendar toCalendar(TimeZone zone) {
        return toCalendar(zone, Locale.getDefault(Locale.Category.FORMAT));
    }

    /**
     * 转换为Calendar
     *
     * @param zone   时区 {@link TimeZone}
     * @param locale 地域 {@link Locale}
     * @return {@link Calendar}
     */
    public Calendar toCalendar(TimeZone zone, Locale locale) {
        if (null == locale) {
            locale = Locale.getDefault(Locale.Category.FORMAT);
        }
        final Calendar cal = (null != zone) ? Calendar.getInstance(zone, locale) : Calendar.getInstance(locale);
        cal.setFirstDayOfWeek(firstDayOfWeek.getValue());
        cal.setTime(this);
        return cal;
    }

    /**
     * 转换为 {@link Date}
     * 考虑到很多框架(例如Hibernate)的兼容性,提供此方法返回JDK原生的Date对象
     *
     * @return {@link Date}
     */
    public Date toJdkDate() {
        return new Date(this.getTime());
    }

    /**
     * 转为{@link Timestamp}
     *
     * @return {@link Timestamp}
     */
    public Timestamp toTimestamp() {
        return new Timestamp(this.getTime());
    }

    /**
     * 转为 {@link java.sql.Date}
     *
     * @return {@link java.sql.Date}
     */
    public java.sql.Date toSqlDate() {
        return new java.sql.Date(getTime());
    }

    /**
     * 计算相差时长
     *
     * @param date 对比的日期
     * @return {@link  Between}
     */
    public Between between(Date date) {
        return new Between(this, date);
    }

    /**
     * 计算相差时长
     *
     * @param date 对比的日期
     * @param unit 单位 {@link Fields.Time}
     * @return 相差时长
     */
    public long between(Date date, Fields.Time unit) {
        return new Between(this, date).between(unit);
    }

    /**
     * 计算相差时长
     *
     * @param date        对比的日期
     * @param unit        单位 {@link  Fields.Time}
     * @param formatLevel 格式化级别
     * @return 相差时长
     */
    public String between(Date date, Fields.Time unit, Fields.Level formatLevel) {
        return new Between(this, date).toString(formatLevel);
    }

    /**
     * 当前日期是否在日期指定范围内
     * 起始日期和结束日期可以互换
     *
     * @param beginDate 起始日期
     * @param endDate   结束日期
     * @return 是否在范围内
     */
    public boolean isIn(Date beginDate, Date endDate) {
        long beginMills = beginDate.getTime();
        long endMills = endDate.getTime();
        long thisMills = this.getTime();

        return thisMills >= Math.min(beginMills, endMills) && thisMills <= Math.max(beginMills, endMills);
    }

    /**
     * 是否在给定日期之前
     *
     * @param date 日期
     * @return 是否在给定日期之前或与给定日期相等
     */
    public boolean isBefore(Date date) {
        if (null == date) {
            throw new NullPointerException("Date to compare is null !");
        }
        return compareTo(date) < 0;
    }

    /**
     * 是否在给定日期之前或与给定日期相等
     *
     * @param date 日期
     * @return 是否在给定日期之前或与给定日期相等
     */
    public boolean isBeforeOrEquals(Date date) {
        if (null == date) {
            throw new NullPointerException("Date to compare is null !");
        }
        return compareTo(date) <= 0;
    }

    /**
     * 是否在给定日期之后或与给定日期相等
     *
     * @param date 日期
     * @return 是否在给定日期之后或与给定日期相等
     */
    public boolean isAfter(Date date) {
        if (null == date) {
            throw new NullPointerException("Date to compare is null !");
        }
        return compareTo(date) > 0;
    }

    /**
     * 是否在给定日期之后或与给定日期相等
     *
     * @param date 日期
     * @return 是否在给定日期之后或与给定日期相等
     */
    public boolean isAfterOrEquals(Date date) {
        if (null == date) {
            throw new NullPointerException("Date to compare is null !");
        }
        return compareTo(date) >= 0;
    }

    /**
     * 对象是否可变
     * 如果为不可变对象，以下方法将返回新方法：
     * <ul>
     * <li>{@link DateTime#offset(Fields.DateField, int)}</li>
     * <li>{@link DateTime#setField(Fields.DateField, int)}</li>
     * <li>{@link DateTime#setField(int, int)}</li>
     * </ul>
     * 如果为不可变对象，{@link DateTime#setTime(long)}将抛出异常
     *
     * @return 对象是否可变
     */
    public boolean isMutable() {
        return mutable;
    }

    /**
     * 设置对象是否可变 如果为不可变对象，以下方法将返回新方法：
     * <ul>
     * <li>{@link DateTime#offset(Fields.DateField, int)}</li>
     * <li>{@link DateTime#setField(Fields.DateField, int)}</li>
     * <li>{@link DateTime#setField(int, int)}</li>
     * </ul>
     * 如果为不可变对象，{@link DateTime#setTime(long)}将抛出异常
     *
     * @param mutable 是否可变
     * @return this
     */
    public DateTime setMutable(boolean mutable) {
        this.mutable = mutable;
        return this;
    }

    /**
     * 获得一周的第一天,默认为周一
     *
     * @return 一周的第一天
     */
    public Fields.Week getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    /**
     * 设置一周的第一天
     * JDK的Calendar中默认一周的第一天是周日,将此默认值设置为周一
     * 设置一周的第一天主要影响{@link #weekOfMonth()}和{@link #weekOfYear()} 两个方法
     *
     * @param firstDayOfWeek 一周的第一天
     * @return this
     * @see #weekOfMonth()
     * @see #weekOfYear()
     */
    public DateTime setFirstDayOfWeek(Fields.Week firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
        return this;
    }

    /**
     * 获取时区
     *
     * @return 时区
     */
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    /**
     * 设置时区
     *
     * @param timeZone 时区
     * @return this
     */
    public DateTime setTimeZone(TimeZone timeZone) {
        this.timeZone = ObjectKit.defaultIfNull(timeZone, TimeZone.getDefault());
        return this;
    }

    /**
     * 获取时区ID
     *
     * @return 时区ID
     */
    public ZoneId getZoneId() {
        return this.timeZone.toZoneId();
    }

    /**
     * 转为"yyyy-MM-dd " 格式字符串
     *
     * @return "yyyy-MM-dd " 格式字符串
     */
    public String toDateStr() {
        if (null != this.timeZone) {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Fields.NORM_DATE_PATTERN);
            simpleDateFormat.setTimeZone(this.timeZone);
            return toString(simpleDateFormat);
        }
        return toString(Fields.NORM_DATE_FORMAT);
    }

    /**
     * 转为"HH:mm:ss" 格式字符串
     *
     * @return "HH:mm:ss" 格式字符串
     */
    public String toTimeStr() {
        if (null != this.timeZone) {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Fields.NORM_TIME_PATTERN);
            simpleDateFormat.setTimeZone(this.timeZone);
            return toString(simpleDateFormat);
        }
        return toString(Fields.NORM_TIME_FORMAT);
    }

    /**
     * @return 输出精确到毫秒的标准日期形式
     */
    public String toMsStr() {
        return toString(Fields.NORM_DATETIME_MS_FORMAT);
    }

    /**
     * 转为"yyyy-MM-dd yyyy-MM-dd HH:mm:ss " 格式字符串
     * 如果时区被设置，会转换为其时区对应的时间，否则转换为当前地点对应的时区
     *
     * @return "yyyy-MM-dd yyyy-MM-dd HH:mm:ss " 格式字符串
     */
    @Override
    public String toString() {
        return toString(this.timeZone);
    }

    /**
     * 转为"yyyy-MM-dd yyyy-MM-dd HH:mm:ss " 格式字符串
     * 如果时区不为{@code null}，会转换为其时区对应的时间，否则转换为当前时间对应的时区
     *
     * @param timeZone 时区
     * @return "yyyy-MM-dd yyyy-MM-dd HH:mm:ss " 格式字符串
     */
    public String toString(TimeZone timeZone) {
        if (null != timeZone) {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Fields.NORM_DATETIME_PATTERN);
            simpleDateFormat.setTimeZone(timeZone);
            return toString(simpleDateFormat);
        }
        return toString(Fields.NORM_DATETIME_FORMAT);
    }

    /**
     * 转为字符串
     *
     * @param format 日期格式，常用格式见： {@link Fields}
     * @return String
     */
    public String toString(String format) {
        if (null != this.timeZone) {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            simpleDateFormat.setTimeZone(this.timeZone);
            return toString(simpleDateFormat);
        }
        return toString(FormatBuilder.getInstance(format));
    }

    /**
     * 转为字符串
     *
     * @param format {@link DatePrinter} 或 {@link FormatBuilder}
     * @return String
     */
    public String toString(DatePrinter format) {
        return format.format(this);
    }

    /**
     * 转为字符串
     *
     * @param format {@link SimpleDateFormat}
     * @return String
     */
    public String toString(DateFormat format) {
        return format.format(this);
    }

    /**
     * 设置日期时间
     *
     * @param time 日期时间毫秒
     * @return this
     */
    private DateTime setTimeInternal(long time) {
        super.setTime(time);
        return this;
    }

}

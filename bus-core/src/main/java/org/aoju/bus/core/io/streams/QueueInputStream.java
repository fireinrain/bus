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
package org.aoju.bus.core.io.streams;

import org.aoju.bus.core.lang.Symbol;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

/**
 * @author Kimi Liu
 * @version 6.1.1
 * @since JDK 1.8+
 */
public class QueueInputStream extends InputStream {

    //原始流
    private InputStream is;
    //缓存
    private LinkedList<Integer> cache = new LinkedList<>();
    //peek索引
    private int peekindex = 0;
    //是否到流尾
    private boolean end = false;
    //列
    private int col = 0;
    //行
    private int row = 1;

    public QueueInputStream(InputStream is) {
        this.is = is;
    }

    public int read() throws IOException {
        return poll();
    }

    /**
     * 读取一项数据
     *
     * @param ends 结束符, 默认' ', '\r', '\n'
     * @return 数据
     * @throws IOException 异常
     */
    public String readItem(char... ends) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            switch (peek()) {
                case Symbol.C_SPACE:
                case Symbol.C_CR:
                case Symbol.C_LF:
                case -1:
                    return sb.toString();
                default:
                    for (Character c : ends) {
                        if (c.charValue() == peek()) {
                            return sb.toString();
                        }
                    }
                    sb.append((char) poll());
            }
        }
    }

    /**
     * 读取一行
     *
     * @return 一行数据
     * @throws IOException 异常
     */
    public String readLine() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (; ; ) {
            int v = peek();
            if (v == Symbol.C_CR || v == Symbol.C_LF) {
                poll();
                v = peekNext();
                if (v == Symbol.C_CR || v == Symbol.C_LF) {
                    poll();
                }
                break;
            }
            sb.append((char) poll());
        }
        return sb.toString();
    }

    /**
     * 读取头部字节, 并删除
     *
     * @return 头部字节
     * @throws IOException 异常
     */
    public int poll() throws IOException {
        peekindex = 0;
        int v = -1;
        if (cache.size() <= 0) {
            v = is.read();
        } else {
            v = cache.poll();
        }
        if (v == -1) {
            end = true;
        }
        if (v == Symbol.C_LF) {
            col = 0;
            row++;
        } else {
            col++;
        }
        return v;
    }

    /**
     * 访问头部开始第几个字节, 不删除
     *
     * @param index 索引
     * @return 头部的第N个字节
     * @throws IOException 异常
     */
    public int peek(int index) throws IOException {
        while (cache.size() <= index) {
            cache.add(is.read());
        }
        return cache.get(index);
    }

    /**
     * 访问上次peekNext访问的下个位置的字节, 未访问过则访问索引0, poll, peek后归零, 不删除
     *
     * @return 下一个位置的字节
     * @throws IOException 异常
     */
    public int peekNext() throws IOException {
        return peek(peekindex++);
    }

    /**
     * 访问头部字节, 不删除
     *
     * @return 头部字节
     * @throws IOException 异常
     */
    public int peek() throws IOException {
        peekindex = 0;
        int v = peek(peekindex++);
        if (v == -1) {
            end = true;
        }
        return v;
    }

    /**
     * 跳过和丢弃输入流中的数据
     */
    public long skip(long n) throws IOException {
        int s = cache.size();
        if (s > 0) {
            if (s < n) {
                n = n - s;
            } else {
                for (int i = 0; i < n; i++) {
                    cache.poll();
                }
                return n;
            }
        }
        return super.skip(n) + s;
    }

    /**
     * 是否结束
     *
     * @return true 如果已经结束
     */
    public boolean isEnd() {
        return end;
    }


    /**
     * 是否以 start 开始
     *
     * @param start 开始位置
     * @return true, 如果的确以指定字符串开始
     * @throws IOException 异常
     */
    public boolean startWith(String start) throws IOException {
        char[] cs = start.toCharArray();
        int i = 0;
        for (; i < cs.length; i++) {
            if (peek(i) != cs[i]) {
                return false;
            }
        }
        return true;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

}
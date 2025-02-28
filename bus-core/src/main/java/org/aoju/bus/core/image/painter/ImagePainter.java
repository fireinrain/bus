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
package org.aoju.bus.core.image.painter;

import org.aoju.bus.core.image.element.AbstractElement;
import org.aoju.bus.core.image.element.ImageElement;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Kimi Liu
 * @version 6.1.1
 * @since JDK 1.8+
 */
public class ImagePainter implements Painter {

    @Override
    public void draw(Graphics2D g, AbstractElement element, int canvasWidth) throws Exception {

        // 强制转成子类
        ImageElement imageElement = (ImageElement) element;

        // 读取元素图
        BufferedImage image = imageElement.getImage();

        // 计算宽高
        int width = 0;
        int height = 0;

        switch (imageElement.getZoomMode()) {
            case ORIGIN:
                width = image.getWidth();
                height = image.getHeight();
                break;
            case WIDTH:
                width = imageElement.getWidth();
                height = image.getHeight() * width / image.getWidth();
                break;
            case HEIGHT:
                height = imageElement.getHeight();
                width = image.getWidth() * height / image.getHeight();
                break;
            case OPTIONAL:
                height = imageElement.getHeight();
                width = imageElement.getWidth();
                break;
        }

        // 设置圆角
        if (imageElement.getRoundCorner() != null) {
            image = makeRoundCorner(image, width, height, imageElement.getRoundCorner());
        }

        // 判断是否设置居中
        if (imageElement.isCenter()) {
            int centerX = (canvasWidth - width) / 2;
            imageElement.setX(centerX);
        }

        // 设置透明度
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, imageElement.getAlpha()));

        // 将元素图绘制到画布
        g.drawImage(image, imageElement.getX(), imageElement.getY(), width, height, null);
    }

    private BufferedImage makeRoundCorner(BufferedImage srcImage, int width, int height, int radius) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.fillRoundRect(0, 0, width, height, radius, radius);
        g.setComposite(AlphaComposite.SrcIn);
        g.drawImage(srcImage, 0, 0, width, height, null);
        g.dispose();
        return image;
    }

}


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
package org.aoju.bus.image.metric.internal.xdsi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Kimi Liu
 * @version 6.1.1
 * @since JDK 1.8+
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PostalAddressType")
public class PostalAddressType {

    @XmlAttribute(name = "city")
    protected String city;
    @XmlAttribute(name = "country")
    protected String country;
    @XmlAttribute(name = "postalCode")
    protected String postalCode;
    @XmlAttribute(name = "stateOrProvince")
    protected String stateOrProvince;
    @XmlAttribute(name = "street")
    protected String street;
    @XmlAttribute(name = "streetNumber")
    protected String streetNumber;

    public String getCity() {
        return this.city;
    }

    public void setCity(String value) {
        this.city = value;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String value) {
        this.country = value;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(String value) {
        this.postalCode = value;
    }

    public String getStateOrProvince() {
        return this.stateOrProvince;
    }

    public void setStateOrProvince(String value) {
        this.stateOrProvince = value;
    }

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String value) {
        this.street = value;
    }

    public String getStreetNumber() {
        return this.streetNumber;
    }

    public void setStreetNumber(String value) {
        this.streetNumber = value;
    }

}


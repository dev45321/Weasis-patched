/*******************************************************************************
 * Copyright (c) 2016 Weasis Team and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nicolas Roduit - initial API and implementation
 *******************************************************************************/
package org.weasis.acquire.explorer.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.weasis.acquire.explorer.AcquireImageInfo;
import org.weasis.acquire.explorer.gui.dialog.AcquirePublishDialog.EResolution;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.api.media.data.TagW;

public class ImageInfoHelper {
    public static final Logger LOGGER = LoggerFactory.getLogger(ImageInfoHelper.class);

    private ImageInfoHelper() {
        super();
    }
    
    /**
     * Calculate the ratio between the image and the given resolution
     * 
     * @param imgInfo
     * @param resolution
     * @return
     */
    public static Double calculateRatio(AcquireImageInfo imgInfo, EResolution resolution, Double max) {
        try {
            Objects.requireNonNull(imgInfo);
            Objects.requireNonNull(resolution);
            Objects.requireNonNull(max);
            
            Double expectedImageSize;
            switch (resolution) {
                case hd:
                    expectedImageSize = max;
                    break;
                case md:
                    expectedImageSize = Math.floor((max * 2) / 3);
                    break;
                default:
                    return null;
            }
    
            ImageElement imgElt =imgInfo.getImage();
            Integer width = (Integer) imgElt.getTagValue(TagW.ImageWidth);
            Integer height = (Integer) imgElt.getTagValue(TagW.ImageHeight);           
            Double currentImageSize = (double) Math.max(width, height);
            return BigDecimal.valueOf(expectedImageSize / currentImageSize).setScale(5, RoundingMode.HALF_UP).doubleValue();
        } catch (NullPointerException e) {
            LOGGER.warn("An error occurs when calculate ratio for : " + imgInfo + ", resolution=> " + resolution, e);
            return null;
        }
    }
}

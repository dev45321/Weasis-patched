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
package org.weasis.core.ui.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlElement;

import org.weasis.core.ui.model.utils.imp.DefaultUUID;

public class ReferencedSeries extends DefaultUUID {
    private static final long serialVersionUID = -6824893762236081155L;

    private List<ReferencedImage> images;

    public ReferencedSeries() {
        this(null, null);
    }

    public ReferencedSeries(String uuid) {
        this(uuid, null);
    }

    public ReferencedSeries(String uuid, List<ReferencedImage> images) {
        super(uuid);
        setImages(images);
    }

    @XmlElement(name = "image")
    public List<ReferencedImage> getImages() {
        return images;
    }

    public void setImages(List<ReferencedImage> images) {
        this.images = Optional.ofNullable(images).orElse(new ArrayList<>());
    }
}

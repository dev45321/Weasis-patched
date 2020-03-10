/*******************************************************************************
 * Copyright (c) 2009-2020 Weasis Team and other contributors.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.weasis.core.api.media.data;

import java.util.Collections;
import java.util.Comparator;

public abstract class SeriesComparator<T> implements Comparator<T> {
    private Comparator<T> inverse;

    public final Comparator<T> getReversOrderComparator() {
        if (inverse == null) {
            inverse = Collections.reverseOrder(this);
        }
        return inverse;
    }
}

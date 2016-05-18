/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.borax12.materialdaterangepicker.date;

import android.content.Context;

/**
 * An adapter for a list of {@link SimpleMonthView} items.
 */
public class SimpleMonthAdapter extends com.borax12.materialdaterangepicker.date.MonthAdapter {

    public SimpleMonthAdapter(Context context, DateRangePickerController controller) {
        super(context, controller);
    }

    @Override
    public com.borax12.materialdaterangepicker.date.MonthView createMonthView(Context context) {
        final com.borax12.materialdaterangepicker.date.MonthView monthView = new com.borax12.materialdaterangepicker.date.SimpleMonthView(context, null, mController);
        return monthView;
    }
}

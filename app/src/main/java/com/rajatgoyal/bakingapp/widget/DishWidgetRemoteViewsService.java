package com.rajatgoyal.bakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by rajat on 4/9/17.
 */

public class DishWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new DishWidgetRemoteViewsFactory(this.getApplicationContext());
    }
}

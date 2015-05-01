package net.crowmaster.esmfamil.util;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

/**
 * Created by root on 4/29/15.
 */
public class HotFixGridLayoutManager  extends GridLayoutManager{
    public HotFixGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }
}

package com.koekoetech.clinic.helper;

import android.view.View;
import me.texy.treeview.base.BaseNodeViewBinder;
import me.texy.treeview.base.BaseNodeViewFactory;

/**
 * Created by ZMN on 9/12/18.
 **/
public class ICDNodeViewFactory extends BaseNodeViewFactory {

    @Override
    public BaseNodeViewBinder getNodeViewBinder(View view, int level) {
        switch (level) {
            case 0:
                return new ChapterNodeBinder(view);
            case 1:
                return new TermGroupNodeBinder(view);
            case 2:
                return new TermNodeBinder(view);
            default:
                return null;
        }
    }
}

package com.koekoetech.clinic.helper;

import android.content.Context;
import android.view.View;
import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import com.koekoetech.clinic.R;
import me.texy.treeview.TreeNode;
import me.texy.treeview.base.BaseNodeViewBinder;

/**
 * Created by ZMN on 9/12/18.
 **/
public class ChapterNodeBinder extends BaseNodeViewBinder {

    private AppCompatTextView tvTitle;
    private AppCompatImageView ivToggleIndicator;
    private Context mContext;

    public ChapterNodeBinder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        tvTitle = itemView.findViewById(R.id.itemChapterNode_tvTitle);
        ivToggleIndicator = itemView.findViewById(R.id.itemChapterNode_ivExpandStatusIndicator);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_chapter_node;
    }

    @Override
    public void bindView(TreeNode treeNode) {
        tvTitle.setText(treeNode.getValue().toString());
        toggleExpandedCollapsedUI(treeNode.isExpanded());
    }

    @Override
    public void onNodeToggled(TreeNode treeNode, boolean expand) {
        toggleExpandedCollapsedUI(expand);

    }

    private void toggleExpandedCollapsedUI(boolean isExpanded) {
        @DrawableRes int toggleIndicatorDrawableId = isExpanded ? R.drawable.ic_drop_up : R.drawable.ic_drop_down;
        ivToggleIndicator.setImageDrawable(ContextCompat.getDrawable(mContext, toggleIndicatorDrawableId));
    }

}

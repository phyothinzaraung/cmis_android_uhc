package com.koekoetech.clinic.helper;

import android.content.Context;
import android.view.View;
import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import com.koekoetech.clinic.R;
import me.texy.treeview.TreeNode;
import me.texy.treeview.base.CheckableNodeViewBinder;

/**
 * Created by ZMN on 9/12/18.
 **/
public class TermGroupNodeBinder extends CheckableNodeViewBinder {

    private AppCompatTextView tvTitle;
    private AppCompatImageView ivCheckStatus;
    private AppCompatImageView ivExpandToggleIndicator;
    private Context mContext;

    public TermGroupNodeBinder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        tvTitle = itemView.findViewById(R.id.itemTermGroupNode_tvTitle);
        ivCheckStatus = itemView.findViewById(R.id.itemTermGroupNode_ivSelectStatus);
        ivExpandToggleIndicator = itemView.findViewById(R.id.itemTermGroupNode_ivExpandStatusIndicator);
    }

    @Override
    public int getCheckableViewId() {
        return R.id.itemTermGroupNode_cbSelect;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_term_group_node;
    }

    @Override
    public void bindView(TreeNode treeNode) {
        tvTitle.setText(treeNode.getValue().toString());
        showCheckedStatus(treeNode.isSelected());
        toggleExpandedCollapsedUI(treeNode.isExpanded());
    }

    @Override
    public void onNodeSelectedChanged(TreeNode treeNode, boolean selected) {
        super.onNodeSelectedChanged(treeNode, selected);
        showCheckedStatus(selected);
    }

    @Override
    public void onNodeToggled(TreeNode treeNode, boolean expand) {
        toggleExpandedCollapsedUI(expand);

    }

    private void showCheckedStatus(boolean isSelected) {
        @DrawableRes int statusImg = isSelected ? R.drawable.done : R.drawable.not_done;
        ivCheckStatus.setImageDrawable(ContextCompat.getDrawable(mContext, statusImg));
    }

    private void toggleExpandedCollapsedUI(boolean isExpanded) {
        @DrawableRes int toggleIndicatorDrawableId = isExpanded ? R.drawable.ic_drop_up : R.drawable.ic_drop_down;
        ivExpandToggleIndicator.setImageDrawable(ContextCompat.getDrawable(mContext, toggleIndicatorDrawableId));
    }
}

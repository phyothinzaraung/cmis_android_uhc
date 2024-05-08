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
public class TermNodeBinder extends CheckableNodeViewBinder {

    private AppCompatTextView tvTitle;
    private AppCompatImageView ivCheckStatus;
    private Context mContext;

    public TermNodeBinder(View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.itemTermNode_tvTitle);
        ivCheckStatus = itemView.findViewById(R.id.itemTermNode_ivSelectStatus);
        mContext = itemView.getContext();
    }

    @Override
    public int getCheckableViewId() {
        return R.id.itemTermNode_cbSelect;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_term_node;
    }

    @Override
    public void bindView(TreeNode treeNode) {
        tvTitle.setText(treeNode.getValue().toString());
        showCheckedStatus(treeNode.isSelected());
    }

    @Override
    public void onNodeSelectedChanged(TreeNode treeNode, boolean selected) {
        super.onNodeSelectedChanged(treeNode, selected);
        showCheckedStatus(selected);
    }

    private void showCheckedStatus(boolean isSelected) {
        @DrawableRes int statusImg = isSelected ? R.drawable.done : R.drawable.not_done;
        ivCheckStatus.setImageDrawable(ContextCompat.getDrawable(mContext, statusImg));
    }
}

package com.koekoetech.clinic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.koekoetech.clinic.R;
import com.koekoetech.clinic.helper.AppUtils;
import com.koekoetech.clinic.helper.ICDNodeViewFactory;
import com.koekoetech.clinic.model.ICDChapterEntity;
import com.koekoetech.clinic.model.ICDTermGroupEntity;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import io.realm.Realm;
import me.texy.treeview.TreeNode;
import me.texy.treeview.TreeView;

public class ICDChooserActivity extends BaseActivity {

    public static final String EXTRA_SELECTED_ICD = "SelectedICDs";
    private static final String TAG = "ICDChooserActivity";

    private FrameLayout viewGroup;
    private EditText edtSearchQuery;
    private ImageButton imgBtnClearQuery;
    private AppCompatButton icdBtnSelect;

    private TreeNode root;
    private TreeView treeView;
    private boolean isSearchInProgress = false;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_icd_chooser;
    }

    @Override
    protected void setUpContents(Bundle savedInstanceState) {

        allFindViewbyId();

        setupToolbar(true);

        setupToolbarText("ICD Terms");

        setupSearch();

        root = TreeNode.root();

        buildTree();

        treeView = new TreeView(root, this, new ICDNodeViewFactory());
        View view = treeView.getView();
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        viewGroup.addView(view);

        icdBtnSelect.setOnClickListener(v -> {
            String selectedNodes = getSelectedNodes();
            if (TextUtils.isEmpty(selectedNodes)) {
                Toast.makeText(getApplication(), "Please select an ICD-10 Term!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_SELECTED_ICD, selectedNodes);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void allFindViewbyId() {
        viewGroup = findViewById(R.id.icdterms_container);
        edtSearchQuery = findViewById(R.id.icdterms_edtSearchQuery);
        imgBtnClearQuery = findViewById(R.id.icdterms_btnClearQuery);
        icdBtnSelect = findViewById(R.id.icdterms_btnSelect);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.icd_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.expand_all) {
            Log.d(TAG, "onOptionsItemSelected: Expand All Clicked");
            treeView.expandAll();
        } else if (itemId == R.id.collapse_all) {
            Log.d(TAG, "onOptionsItemSelected: Collapse All Clicked");
            treeView.collapseAll();
        } else if (itemId == R.id.expand_groups) {
            Log.d(TAG, "onOptionsItemSelected: Expand Groups Clicked");
            treeView.expandLevel(0);
            treeView.expandLevel(1);
        } else if (itemId == R.id.collapse_groups) {
            Log.d(TAG, "onOptionsItemSelected: Collapse Groups Clicked");
            treeView.collapseLevel(1);
        } else if (itemId == R.id.deselect_all) {
            Log.d(TAG, "onOptionsItemSelected: Deselected All Clicked");
            treeView.deselectAll();
        } else if (itemId == R.id.expand_select_nodes) {
            Log.d(TAG, "onOptionsItemSelected: Expand Selected Nodes Clicked");
            expandSelectedNodes();
        } else if (itemId == R.id.show_select_node) {
            Log.d(TAG, "onOptionsItemSelected: Show Selected Nodes Clicked");
            showSelection();
        }

        return super.onOptionsItemSelected(item);
    }

    private void expandSelectedNodes() {
        treeView.collapseAll();
        for (TreeNode termNode : treeView.getSelectedNodes()) {
            TreeNode groupNode = termNode.getParent();
            TreeNode chapterNode = groupNode.getParent();
            if (!chapterNode.isExpanded()) {
                treeView.expandNode(chapterNode);
            }
            if (!groupNode.isExpanded()) {
                treeView.expandNode(groupNode);
            }
        }
    }

    private void showSelection() {
        String selectedNodes = getSelectedNodes();
        if (!TextUtils.isEmpty(selectedNodes)) {
            new AlertDialog.Builder(this)
                    .setTitle("Selection")
                    .setMessage(selectedNodes)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        } else {
            Toast.makeText(this, "No Selection Yet", Toast.LENGTH_SHORT).show();
        }
    }

    private String getSelectedNodes() {
        List<TreeNode> selectedNodes = new ArrayList<>(treeView.getSelectedNodes());
        List<TreeNode> processedSelectedNodes = new ArrayList<>(selectedNodes);
        StringBuilder selectedResult = new StringBuilder();

        for (TreeNode termNode : selectedNodes) {
            if (termNode.getLevel() == 1) {
                List<TreeNode> children = termNode.getChildren();
                processedSelectedNodes.removeAll(children);
            }
        }

        for (TreeNode node : processedSelectedNodes) {
            String selectionText = "ICD10 : ";

            if (node.getLevel() == 1) {
                TreeNode chapterNode = node.getParent();
                Object chapterValue = chapterNode.getValue();
                Object nodeValue = node.getValue();

                if (chapterValue != null && nodeValue != null) {
                    selectionText += chapterValue.toString() + " > ";
                    selectionText += nodeValue.toString() + ";";
                    selectedResult.append(selectionText);
                    selectedResult.append("\n");
                }
            } else if (node.getLevel() == 2) {
                TreeNode groupNode = node.getParent();
                TreeNode chapterNode = groupNode.getParent();

                Object chapterValue = chapterNode.getValue();
                Object groupValue = groupNode.getValue();
                Object termNodeValue = node.getValue();

                if (chapterValue != null && groupValue != null && termNodeValue != null) {
                    selectionText += chapterValue.toString() + " > ";
                    selectionText += groupValue.toString() + " > ";
                    selectionText += termNodeValue.toString() + ";";
                    selectedResult.append(selectionText);
                    selectedResult.append("\n");
                }
            }

        }

        String selectedNodesStr = selectedResult.toString();

        if (!TextUtils.isEmpty(selectedNodesStr)) {
            selectedNodesStr = selectedNodesStr.substring(0, selectedNodesStr.length() - 2);
        }

        return selectedNodesStr;
    }

    private void buildTree() {
        try (Realm realm = Realm.getDefaultInstance()) {
            List<ICDChapterEntity> chapters = realm.where(ICDChapterEntity.class).findAll();
            for (ICDChapterEntity chapter : chapters) {
                TreeNode chapterNode = new TreeNode(chapter.getTitle());
                chapterNode.setLevel(0);

                for (ICDTermGroupEntity icdTermGroup : chapter.getTermsGroup()) {
                    TreeNode termGroupNode = new TreeNode(icdTermGroup.getTitle());
                    termGroupNode.setLevel(1);

                    for (String term : icdTermGroup.getTerms()) {
                        TreeNode termNode = new TreeNode(term);
                        termNode.setLevel(2);
                        termGroupNode.addChild(termNode);
                    }

                    chapterNode.addChild(termGroupNode);
                }

                root.addChild(chapterNode);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setupSearch() {
        edtSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                imgBtnClearQuery.setVisibility(TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE);
            }
        });

        edtSearchQuery.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = edtSearchQuery.getText().toString().trim();
                searchICD(query);
                return true;
            }
            return false;
        });

        imgBtnClearQuery.setOnClickListener(v -> edtSearchQuery.setText(""));
    }

    private void searchICD(String query) {

        Log.d(TAG, "searchICD() called with: query = [" + query + "]");

        if (isSearchInProgress || TextUtils.isEmpty(query)) {
            return;
        }

        AppUtils.dismissSoftKeyboard(this);

        isSearchInProgress = true;

        treeView.collapseAll();
        List<TreeNode> chapterNodes = treeView.getAllNodes();
        for (TreeNode chapterNode : chapterNodes) {
            String chapter = chapterNode.getValue().toString();
            if (doesSearchMatch(query, chapter)) {
                treeView.expandNode(chapterNode);
            }

            for (TreeNode termsGroupNode : chapterNode.getChildren()) {
                boolean doesMatchFoundInTermGroup = false;
                String termGroup = termsGroupNode.getValue().toString();
                if (doesSearchMatch(query, termGroup)) {
                    doesMatchFoundInTermGroup = true;
                    if(!chapterNode.isExpanded()){
                        treeView.expandNode(chapterNode);
                    }
                    treeView.expandNode(termsGroupNode);
                }

                if(!doesMatchFoundInTermGroup) {
                    for(TreeNode termNode: termsGroupNode.getChildren()){
                        String term = termNode.getValue().toString();
                        if(doesSearchMatch(query,term)){
                            if(!chapterNode.isExpanded()){
                                treeView.expandNode(chapterNode);
                            }
                            if (!termsGroupNode.isExpanded()) {
                                treeView.expandNode(termsGroupNode);
                            }
                            break;
                        }
                    }
                }

            }
        }

        isSearchInProgress = false;
    }

    private boolean doesSearchMatch(String query, String target) {
        Log.d(TAG, "doesSearchMatch() called with: query = [" + query + "], target = [" + target + "]");
        String lowerCasedQuery = TextUtils.isEmpty(query) ? "" : query.toLowerCase();
        String lowerCasedTarget = TextUtils.isEmpty(target) ? "" : target.toLowerCase();
        boolean doesMatch = lowerCasedTarget.contains(lowerCasedQuery) || TextUtils.equals(lowerCasedQuery, lowerCasedTarget);
        Log.d(TAG, "doesSearchMatch() returned: " + doesMatch);
        return doesMatch;
    }

}

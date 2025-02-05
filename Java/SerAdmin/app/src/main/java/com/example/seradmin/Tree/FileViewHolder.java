package com.example.seradmin.Tree;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.seradmin.R;
import com.example.seradmin.Tree.ControladoresTree.TreeNode;
import com.example.seradmin.Tree.ControladoresTree.TreeViewHolder;

public class FileViewHolder extends TreeViewHolder {

    private TextView fileName;
    private ImageView fileStateIcon;
    private ImageView fileTypeIcon;
    private Button deleteButton;

    public FileViewHolder(@NonNull View itemView) {
        super(itemView);
        initViews();
    }

    private void initViews() {
        fileName = itemView.findViewById(R.id.file_name);
        fileStateIcon = itemView.findViewById(R.id.file_state_icon);
        fileTypeIcon = itemView.findViewById(R.id.file_type_icon);
        //deleteButton = itemView.findViewById(R.id.delete_button);
    }

    @Override
    public void bindTreeNode(TreeNode node) {
        super.bindTreeNode(node);

        String fileNameStr = node.getValue().toString();
        fileName.setText(fileNameStr);

        int dotIndex = fileNameStr.indexOf('.');
        if (dotIndex == -1) {
            fileTypeIcon.setImageResource(R.drawable.folder);
        } else {
            String extension = fileNameStr.substring(dotIndex);
            int extensionIcon = ExtensionTable.getExtensionIcon(extension);
            fileTypeIcon.setImageResource(extensionIcon);
        }

        if (node.getChildren().isEmpty()) {
            fileStateIcon.setVisibility(View.INVISIBLE);
        } else {
            fileStateIcon.setVisibility(View.VISIBLE);
            int stateIcon = node.isExpanded() ? R.drawable.down_arrow : R.drawable.next;
            fileStateIcon.setImageResource(stateIcon);
        }


    }
}

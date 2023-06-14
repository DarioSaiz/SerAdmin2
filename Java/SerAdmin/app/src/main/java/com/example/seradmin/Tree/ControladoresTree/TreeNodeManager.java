package com.example.seradmin.Tree.ControladoresTree;

import java.util.LinkedList;
import java.util.List;

public class TreeNodeManager {
    private final LinkedList<TreeNode> listaNodos;
    public TreeNodeManager() {
        this.listaNodos = new LinkedList<>();
    }

    public void setTreeNodes(List<TreeNode> treeNodes) {
        listaNodos.clear();
        listaNodos.addAll(treeNodes);
    }

    public List<TreeNode> getTreeNodes() {
        return listaNodos;
    }

    /**
     * Get TreeNode from the current nodes by index
     * @param index of node to get it
     * @return TreeNode from by index from current tree nodes if exists
     */
    public TreeNode get(int index) {
        return listaNodos.get(index);
    }

    /**
     * Add new node to the current tree nodes
     * @param node to add it to the current tree nodes
     * @return true of this node is added
     */
    public boolean addNode(TreeNode node) {
        return listaNodos.add(node);
    }

    /**
     * Clear the current nodes and insert new nodes
     * @param newNodes to update the current nodes with them
     */
    public void updateNodes(List<TreeNode> newNodes) {
        listaNodos.clear();
        listaNodos.addAll(newNodes);
    }


    /**
     * Delete one node from the visible nodes
     * @param node to delete it from the current nodes
     * @return true of this node is deleted
     */
    public boolean removeNode(TreeNode node) {
        return listaNodos.remove(node);
    }

    /**
     * Clear the current nodes
     */
    public void clearNodes() {
        listaNodos.clear();
    }

    /**
     * Get the current number of visible nodes
     * @return the size of visible nodes
     */
    public int size() {
        return listaNodos.size();
    }

    /**
     * Collapsing node and all of his children
     * @param node The node to collapse it
     * @return the index of this node if it exists in the list
     */
    public int collapseNode(TreeNode node) {
        int position = listaNodos.indexOf(node);
        if (position != -1 && node.isExpanded()) {
            node.setExpanded(false);
            LinkedList<TreeNode> deletedParents = new LinkedList<>(node.getChildren());
            listaNodos.removeAll(node.getChildren());
            for (int i = position + 1; i < listaNodos.size(); i++) {
                TreeNode iNode = listaNodos.get(i);
                if (deletedParents.contains(iNode.getParent())) {
                    deletedParents.add(iNode);
                    deletedParents.addAll(iNode.getChildren());
                }
            }
            listaNodos.removeAll(deletedParents);
        }
        return position;
    }

    /**
     * Expanding node and all of his children
     * @param node The node to expand it
     * @return the index of this node if it exists in the list
     */
    public int expandNode(TreeNode node) {
        int position = listaNodos.indexOf(node);
        if (position != -1 && !node.isExpanded()) {
            node.setExpanded(true);
            listaNodos.addAll(position + 1, node.getChildren());
            for (TreeNode child : node.getChildren()) {
                if (child.isExpanded()) updateExpandedNodeChildren(child);
            }
        }
        return position;
    }

    /**
     * Update the list for expanded node
     * to expand any child of his children that is already expanded before
     * @param node that just expanded now
     */
    private void updateExpandedNodeChildren(TreeNode node) {
        int position = listaNodos.indexOf(node);
        if (position != -1 && node.isExpanded()) {
            listaNodos.addAll(position + 1, node.getChildren());
            for (TreeNode child : node.getChildren()) {
                if (child.isExpanded()) updateExpandedNodeChildren(child);
            }
        }
    }

    /**
     *
     * @param  node The node to collapse the branch of it
     * @return the index of this node if it exists in the list
     */
    public int collapseNodeBranch(TreeNode node) {
        int position = listaNodos.indexOf(node);
        if (position != -1 && node.isExpanded()) {
            node.setExpanded(false);
            for (TreeNode child : node.getChildren()) {
                if (!child.getChildren().isEmpty()) collapseNodeBranch(child);
                listaNodos.remove(child);
            }
        }
        return position;
    }

    /**
     * Expanding node full branches
     * @param  node The node to expand the branch of it
     * @return the index of this node if it exists in the list
     */
    public int expandNodeBranch(TreeNode node) {
        int position = listaNodos.indexOf(node);
        if (position != -1 && !node.isExpanded()) {
            node.setExpanded(true);
            int index = position + 1;
            for (TreeNode child : node.getChildren()) {
                int before = listaNodos.size();
                listaNodos.add(index, child);
                expandNodeBranch(child);
                int after = listaNodos.size();
                int diff = after - before;
                index += diff;
            }
        }
        return position;
    }

    /**
     * Expanding one node branch to until specific level
     * @param node to expand branch of it until level
     * @param level to expand node branches to it
     */
    public void expandNodeToLevel(TreeNode node, int level) {
        if (node.getLevel() <= level) expandNode(node);
        for (TreeNode child : node.getChildren()) {
            expandNodeToLevel(child, level);
        }
    }

    /**
     * Expanding all tree nodes branches to until specific level
     * @param level to expand all nodes branches to it
     */
    public void expandNodesAtLevel(int level) {
        for (int i = 0; i < listaNodos.size() ; i++) {
            TreeNode node = listaNodos.get(i);
            expandNodeToLevel(node, level);
        }
    }

    /**
     * Collapsing all nodes in the tree with their children
     */
    public void collapseAll() {
        List<TreeNode> treeNodes = new LinkedList<>();
        for (int i = 0; i < listaNodos.size(); i++) {
            TreeNode root = listaNodos.get(i);
            if (root.getLevel() == 0) {
                collapseNodeBranch(root);
                treeNodes.add(root);
            } else {
                root.setExpanded(false);
            }
        }
        updateNodes(treeNodes);
    }

    /**
     * Expanding all nodes in the tree with their children
     */
    public void expandAll() {
        for (int i = 0; i < listaNodos.size(); i++) {
            TreeNode root = listaNodos.get(i);
            expandNodeBranch(root);
        }
    }
    public TreeNode getRootNode() {
        if (!listaNodos.isEmpty()) {
            return listaNodos.getFirst();
        }
        return null;
    }
}
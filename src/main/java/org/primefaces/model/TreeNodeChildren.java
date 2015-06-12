/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class TreeNodeChildren extends TreeNodeList {

    private TreeNode parent;
    
    public TreeNodeChildren(TreeNode parent) {
        this.parent = parent;
    }
    
    private void eraseParent(TreeNode node) {
        TreeNode parentNode = node.getParent();
        if(parentNode != null) {
            parentNode.getChildren().remove(node);
            node.setParent(null);
        }
    }
    
    @Override
    public boolean add(TreeNode node) {
        if(node == null) {
            throw new NullPointerException();
        }
        else {
            int oldSize = size();
            eraseParent(node);
            boolean result = super.add(node);
            node.setParent(parent);
            updateRowKeys(parent, oldSize);
            return result;
        }
    }

    @Override
    public void add(int index, TreeNode node) {
        if(node == null) {
            throw new NullPointerException();
        }
        else if((index < 0) || (index > size())) {
            throw new IndexOutOfBoundsException();
        } 
        else {
            eraseParent(node);
            super.add(index, node);
            node.setParent(parent);
            updateRowKeys(parent, index);
        }
    }
    
    @Override
    public boolean addAll(Collection<? extends TreeNode> collection) {
        Iterator<TreeNode> elements = (new ArrayList<TreeNode>(collection)).iterator();
        boolean changed = false;
        int oldSize = size();
        while(elements.hasNext()) {
            TreeNode node = elements.next();
            if(node == null) {
                throw new NullPointerException();
            } 
            else {
                eraseParent(node);
                super.add(node);
                node.setParent(parent);
                changed = true;
            }
        }
        
        if(changed) {
            updateRowKeys(parent, oldSize);
        }
        
        return (changed);
    }
    
    @Override
    public boolean addAll(int index, Collection<? extends TreeNode> collection) {
        Iterator<TreeNode> elements = (new ArrayList<TreeNode>(collection)).iterator();
        boolean changed = false;
        int currentIndex = index;
        while(elements.hasNext()) {
            TreeNode node = elements.next();
            if(node == null) {
                throw new NullPointerException();
            } 
            else {
                eraseParent(node);
                super.add(currentIndex++, node);
                node.setParent(parent);
                changed = true;
            }
        }
        
        if(changed) {
            updateRowKeys(parent, index);
        }
        
        return (changed);
    }
    
    @Override
    public TreeNode set(int index, TreeNode node) {
        if(node == null) {
            throw new NullPointerException();
        }
        else if ((index < 0) || (index >= size())) {
            throw new IndexOutOfBoundsException();
        } 
        else {
            if(!parent.equals(node.getParent())) {
                eraseParent(node);
            }
            
            TreeNode previous = super.set(index, node);
            previous.setParent(null);
            node.setParent(parent);
            updateRowKeys(parent, index, index + 1);
            return previous;
        }
    }
    
    /**
     * Optimized set implementation to be used in sorting.
     * @param index index of the element to replace
     * @param node node to be stored at the specified position
     * @return the node previously at the specified position
     */
    public TreeNode setSibling(int index, TreeNode node) {
        if(node == null) {
            throw new NullPointerException();
        }
        else if ((index < 0) || (index >= size())) {
            throw new IndexOutOfBoundsException();
        } 
        else {
            if(!parent.equals(node.getParent())) {
                eraseParent(node);
            }
            
            TreeNode previous = super.set(index, node);
            node.setParent(parent);
            return previous;
        }
    }

    @Override
    public TreeNode remove(int index) {
        TreeNode node = get(index);
        node.clearParent();
        super.remove(index);
        updateRowKeys(parent, index);
        return node;
    }

    @Override
    public boolean remove(Object object) {
        TreeNode node = (TreeNode) object;
        if(node == null) {
            throw new NullPointerException();
        }

        int nodeIndex = super.indexOf(node);
        if(nodeIndex != -1) {
            node.clearParent();
            super.remove(node);
            updateRowKeys(parent, nodeIndex);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void updateRowKeys() {
        updateRowKeys(parent, 0);
    }

    private void updateRowKeys(TreeNode node, int beginIndex) {
        updateRowKeys(node, beginIndex, node.getChildCount());
    }

    private void updateRowKeys(TreeNode node, int beginIndex, int endIndex) {
        if(endIndex > beginIndex) {
            for(int i = beginIndex; i < endIndex; i++) {
                TreeNode childNode = node.getChildren().get(i);

                String childRowKey = (node.getParent() == null) ? String.valueOf(i) : node.getRowKey() + "_" + i;
                if(!childRowKey.equals(childNode.getRowKey())) {
                    childNode.setRowKey(childRowKey);
                    updateRowKeys(childNode, beginIndex);
                }
            }
        }
    }
}

package org.example

import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock

class CoarseGrainedTree<K : Comparable<K>, V>: Tree<K, V> {
    private var root: TreeNode<K, V>? = null
    private val lock = reentrantLock()

    override fun insert(key: K, value: V) {
        lock.withLock {
            root = insertRec(root, key, value)
        }
    }

    private fun insertRec(node: TreeNode<K, V>?, key: K, value: V): TreeNode<K, V> {
        if (node == null) {
            return TreeNode(key, value)
        }
        if (key < node.key) {
            node.left = insertRec(node.left, key, value)
        } else if (key > node.key) {
            node.right = insertRec(node.right, key, value)
        } else {
            node.value = value
        }
        return node
    }

    override fun search(key: K): V? {
        lock.withLock {
            return searchRec(root, key)
        }
    }

    private fun searchRec(node: TreeNode<K, V>?, key: K): V? {
        if (node == null) {
            return null
        }
        return when {
            key < node.key -> searchRec(node.left, key)
            key > node.key -> searchRec(node.right, key)
            else -> node.value
        }
    }

    override fun delete(key: K) {
        lock.withLock {
            root = deleteRec(root, key)
        }
    }

    private fun deleteRec(node: TreeNode<K, V>?, key: K): TreeNode<K, V>? {
        if (node == null) {
            return null
        }
        when {
            key < node.key -> node.left = deleteRec(node.left, key)
            key > node.key -> node.right = deleteRec(node.right, key)
            else -> {
                if (node.left == null) return node.right
                if (node.right == null) return node.left
                val minNode = leftmostNode(node.right!!)
                node.key = minNode.key
                node.value = minNode.value
                node.right = deleteRec(node.right, node.key)
            }
        }
        return node
    }

    private fun leftmostNode(node: TreeNode<K, V>): TreeNode<K, V> {
        var current = node
        while (current.left != null) {
            current = current.left!!
        }
        return current
    }

    override fun getKeys(): List<K> {
        val keys = mutableListOf<K>()
        inOrderTraversal(root, keys)
        return keys
    }

    private fun inOrderTraversal(node: TreeNode<K, V>?, keys: MutableList<K>) {
        node ?: return
        inOrderTraversal(node.left, keys)
        keys.add(node.key)
        inOrderTraversal(node.right, keys)
    }
}

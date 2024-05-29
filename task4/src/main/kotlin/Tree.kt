package org.example

interface Tree<K : Comparable<K>, V> {
    fun insert(key: K, value: V)
    fun search(key: K): V?
    fun delete(key: K)
    fun getKeys(): List<K>
}

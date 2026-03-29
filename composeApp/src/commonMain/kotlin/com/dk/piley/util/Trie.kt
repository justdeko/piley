package com.dk.piley.util

import kotlin.time.Instant

class Trie {
    private inner class TrieNode {
        val children: MutableMap<Char, TrieNode> = mutableMapOf()
        var terminalTitle: String? = null
        var lastCompletion: Instant? = null
    }

    private var root = TrieNode()

    fun insert(title: String, lastCompletion: Instant? = null) {
        var node = root
        for (ch in title.lowercase()) {
            node = node.children.getOrPut(ch) { TrieNode() }
        }
        node.terminalTitle = title
        node.lastCompletion = lastCompletion
    }

    fun search(prefix: String, maxResults: Int = 5): List<String> {
        if (prefix.isBlank()) return emptyList()
        var node = root
        for (ch in prefix.lowercase()) {
            node = node.children[ch] ?: return emptyList()
        }
        val results = mutableListOf<Pair<String, Instant?>>()
        val queue = ArrayDeque<TrieNode>()
        queue.addLast(node)
        while (queue.isNotEmpty() && results.size < maxResults) {
            val current = queue.removeFirst()
            current.terminalTitle?.let { results.add(it to current.lastCompletion) }
            current.children.values.forEach { queue.addLast(it) }
        }
        return results.sortedByDescending { it.second }.map { it.first }
    }

    fun clear() {
        root = TrieNode()
    }
}

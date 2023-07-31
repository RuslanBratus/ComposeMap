package com.example.composemap.ui.navigation.extensions

private const val GRAPH_POSTFIX = "_graph"

fun String.addGraphPostfix(): String {
    return this.plus(GRAPH_POSTFIX)
}
package com.editor.shared

class SysInfo {
    companion object {
        val userName = System.getProperty("user.name")
        val userHome = System.getProperty("user.home")
        val hostname = java.net.InetAddress.getLocalHost().hostName
        val hostAddress = java.net.InetAddress.getLocalHost().hostAddress
        val osName = System.getProperty("os.name")
        val osVersion = System.getProperty("os.version")
        val osArch = System.getProperty("os.arch")
        val processors = Runtime.getRuntime().availableProcessors()
        val freeMemory =  Runtime.getRuntime().freeMemory()
        val totalMemory = Runtime.getRuntime().totalMemory()
    }
}

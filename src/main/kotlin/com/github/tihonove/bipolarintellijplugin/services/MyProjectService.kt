package com.github.tihonove.bipolarintellijplugin.services

import com.github.tihonove.bipolarintellijplugin.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}

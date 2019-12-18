package com.jokerwan.demo.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class JokerWanTransform extends Transform {

    private static Project project
    private static final String NAME = "JokerWanAutoTrack"

    JokerWanTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return NAME
    }

    /**
     * 需要处理的数据类型，有两种枚举类型
     * CLASSES 代表处理的 java 的 class 文件，RESOURCES 代表要处理 java 的资源
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 指 Transform 要操作内容的范围，官方文档 Scope 有 7 种类型：
     * 1. EXTERNAL_LIBRARIES        只有外部库
     * 2. PROJECT                   只有项目内容
     * 3. PROJECT_LOCAL_DEPS        只有项目的本地依赖(本地jar)
     * 4. PROVIDED_ONLY             只提供本地或远程依赖项
     * 5. SUB_PROJECTS              只有子项目。
     * 6. SUB_PROJECTS_LOCAL_DEPS   只有子项目的本地依赖项(本地jar)。
     * 7. TESTED_CODE               由当前变量(包括依赖项)测试的代码
     * @return
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    static void printCopyRight() {
        println()
        println("******************************************************************************")
        println("******                                                                  ******")
        println("******                欢迎使用 JokerWanTransform 编译插件               ******")
        println("******                                                                  ******")
        println("******************************************************************************")
        println()
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        printCopyRight()

        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider()

        // Transform 的 inputs 有两种类型，一种是目录，一种是 jar 包，要分开遍历
        transformInvocation.inputs.each { TransformInput input ->
            input.jarInputs.each { JarInput jarInput ->
                // 处理jar
                processJarInput(jarInput, outputProvider)
            }

            input.directoryInputs.each { DirectoryInput directoryInput ->
                // 处理源码文件
                processDirectoryInput(directoryInput, outputProvider)
            }
        }

    }

    void processJarInput(JarInput jarInput, TransformOutputProvider outputProvider) {
        // 重命名输出文件（同目录copyFile会冲突）
        def jarName = jarInput.name
        def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
        if (jarName.endsWith(".jar")) {
            jarName = jarName.substring(0, jarName.length() - 4)
        }

        File dest = outputProvider.getContentLocation(
                jarName + md5Name,
                jarInput.getContentTypes(),
                jarInput.getScopes(),
                Format.JAR
        )

        // TODO do some transform

        // 将 input 的目录复制到 output 指定目录
        FileUtils.copyFile(jarInput.getFile(), dest)
    }

    void processDirectoryInput(DirectoryInput directoryInput, TransformOutputProvider outputProvider) {
        File dest = outputProvider.getContentLocation(
                directoryInput.getName(),
                directoryInput.getContentTypes(),
                directoryInput.getScopes(),
                Format.DIRECTORY
        )

        // TODO do some transform

        // 将 input 的目录复制到 output 指定目录
        FileUtils.copyDirectory(directoryInput.getFile(), dest)
    }
}
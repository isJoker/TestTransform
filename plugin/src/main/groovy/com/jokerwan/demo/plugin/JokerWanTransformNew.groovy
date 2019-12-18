package com.jokerwan.demo.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class JokerWanTransformNew extends Transform {

    private static Project project
    private static final String NAME = "JokerWanAutoTrack"

    public JokerWanTransformNew(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return NAME
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

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
        println("******                 欢迎使用 JokerWanTransform 编译插件                 ******")
        println("******                                                                  ******")
        println("******************************************************************************")
        println()
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        printCopyRight()

        // OutputProvider管理输出路径，如果消费型输入为空，你会发现OutputProvider == null
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider()

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
        File dest = outputProvider.getContentLocation(
                jarInput.getFile().getAbsolutePath(),
                jarInput.getContentTypes(),
                jarInput.getScopes(),
                Format.JAR
        )

        // TODO do some transform

        // 将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
        FileUtils.copyFile(jarInput.getFile(), dest)
    }

    void processDirectoryInput(DirectoryInput directoryInput, TransformOutputProvider outputProvider) {
        File dest = outputProvider.getContentLocation(
                directoryInput.getName(),
                directoryInput.getContentTypes(),
                directoryInput.getScopes(),
                Format.DIRECTORY
        )

        // 建立文件夹
        FileUtils.forceMkdir(dest)

        // TODO do some transform

        // 将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
        FileUtils.copyDirectory(directoryInput.getFile(), dest)
    }
}
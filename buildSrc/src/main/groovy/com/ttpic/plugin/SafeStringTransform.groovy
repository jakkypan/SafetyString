package com.ttpic.plugin

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.Status
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.build.api.transform.Format
import com.android.utils.FileUtils
import com.google.common.io.Files
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

/**
 * Created by panda on 2018/12/24
 **/
public class SafeStringTransform extends Transform {
    Project project

    String mKey
    String mImplementation
    String[] mSensitives

    List<File> filelist = new ArrayList<>()

    SafeStringTransform(Project project) {
        this.project = project

        // 读取extend配置信息
        project.afterEvaluate {
            mKey = project.ttpicextend.key
            mImplementation = project.ttpicextend.implementation
            mSensitives = project.ttpicextend.sensitives

            if (mKey == null || mKey.length() == 0) {
                throw new IllegalArgumentException("Miss key config")
            }
            if (mImplementation == null || mImplementation.length() == 0) {
                throw new IllegalArgumentException("Miss implementation config")
            }

            println "mKey: $mKey"
            println "mImplementation: $mImplementation"
            println "mSensitives: $mSensitives"
        }
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        println "enter transform process..."

        String dynamicCodeDir = ""
        def originBuildConfigFile = ""
        def revisedBuildConfigFile = ""

        // Transform的inputs有两种类型，一种是目录，一种是jar包
        Collection<TransformInput> inputs = transformInvocation.getInputs()
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider()
        boolean isIncremental = transformInvocation.isIncremental()
        //如果非增量，则清空旧的输出内容
        if(!isIncremental) {
            outputProvider.deleteAll()
        }

        for(TransformInput input : inputs) {
            // 处理jar里的class文件，这里不会对jar进行修改，所以直接全文件夹拷贝
            for(JarInput jarInput : input.getJarInputs()) {
                Status status = jarInput.getStatus()
                File dest = outputProvider.getContentLocation(
                        jarInput.getFile().getAbsolutePath(),
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR)
                if (isIncremental) {
                    switch(status) {
                        case Status.NOTCHANGED:  // 当前文件不需处理，甚至复制操作都不用
                            break
                        case Status.ADDED:
                        case Status.CHANGED:  // 正常处理，输出给下一个任务
                            FileUtils.copyFile(jarInput.getFile(), dest)
                            break
                        case Status.REMOVED:  // 移除outputProvider获取路径对应的文件
                            if (dest.exists()) {
                                org.apache.commons.io.FileUtils.forceDelete(dest)
                            }
                            break
                    }
                } else {
                    FileUtils.copyFile(jarInput.getFile(), dest)
                }
            }

            // 处理文件目录的class文件
            Collection<DirectoryInput> dirInputs = input.getDirectoryInputs()
            for(DirectoryInput directoryInput : dirInputs) {
                File destDir = outputProvider.getContentLocation(
                        directoryInput.getName(),
                        directoryInput.getContentTypes(),
                        directoryInput.getScopes(),
                        Format.DIRECTORY)
                FileUtils.mkdirs(destDir)
                // 遍历每个源文件
                dirInputs.each { dirInput ->
//                    if (destDir.exists()) {
//                        destDir.deleteDir()
//                    }

                    getFileList(dirInput.getFile())
                    for (File fileInput : filelist) {
                        File fileOutput = new File(fileInput.getAbsolutePath().replace(dirInput.file.getAbsolutePath(), destDir.getAbsolutePath()))
                        FileUtils.mkdirs(fileOutput.parentFile)
                        if (dynamicCodeDir == "") {
                            dynamicCodeDir = parseDir(fileOutput.absolutePath)
                        }
                        if (isIncremental && !fileInput.getName().endsWith('BuildConfig.class')) {
                            Status status = fileInput.getValue()
                            switch (status) {
                                case Status.NOTCHANGED:
                                    break
                                case Status.REMOVED:
                                    if(fileOutput.exists()) {
                                        fileOutput.delete()
                                    }
                                    break
                                case Status.ADDED:
                                case Status.CHANGED:
                                    Files.copy(fileInput, fileOutput)
                                    break
                            }
                        } else if (fileInput.getName().endsWith('BuildConfig.class')) {
                            originBuildConfigFile = fileInput
                            revisedBuildConfigFile = fileOutput

                            // 进行ASM处理
                            InputStream is = new BufferedInputStream(new FileInputStream(fileInput))
                            OutputStream os = new BufferedOutputStream(new FileOutputStream(fileOutput))
                            ClassReader cr = new ClassReader(is)
                            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES)
                            cr.accept(new AsmClassReviser(cw, mKey, mImplementation, mSensitives), 0)
                            os.write(cw.toByteArray())
                            os.flush()
                            os.close()
                        } else {
                            Files.copy(fileInput, fileOutput)
                        }
                    }
                }
            }
        }

        // 动态ASM生成类
        new IEncryptionDump().dump(dynamicCodeDir)
        new XorEncryptionDump().dump(dynamicCodeDir)
        new DecoderDump(mImplementation.replaceAll("\\.", "/"), mKey).dump(dynamicCodeDir)

        println "exit transform process..."
    }

    @Override
    String getName() {
        return "SafeString"
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
        return true
    }

    /**
     * 只需要过滤出BuildConfig的class
     *
     * @param dir
     * @return
     */
    List<File> getFileList(File dir) {
        File[] files = dir.listFiles()
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    getFileList(files[i])
                } else  {  // if (files[i].getName().endsWith("BuildConfig.class"))
                    filelist.add(files[i])
                }
            }
        }
        return filelist
    }

    /**
     * 解析出存放ASM class文件的路径
     *
     * @param oldPath
     * @return
     */
    String parseDir(String oldPath) {
        char[] chars = oldPath.toCharArray()
        int index = 0
        char lastChar = 'a'
        for (int i = 0; i < chars.length; i++) {
            if (Character.isDigit(lastChar) && chars[i] == '/') {
                index = i
                break
            }
            lastChar = chars[i]
        }
        return oldPath.substring(0, index)
    }
}

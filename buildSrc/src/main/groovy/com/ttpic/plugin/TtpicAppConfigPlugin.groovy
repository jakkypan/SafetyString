package com.ttpic.plugin

import com.android.build.api.dsl.extension.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by panda on 2018/12/24
 **/
public class TtpicAppConfigPlugin implements Plugin<Project> {

    // 是否打开LOG的显示
    static final ENABLE_LOG = "ENABLE_LOG"
    // 模板解析是否是测试模式，XML/Dat
    static final TPL_PARSER_DEBUG = "TPL_PARSER_DEBUG"
    // 是否显示引导页
    static final HIDE_APP_TUTORIAL = "HIDE_APP_TUTORIAL"
    // 是否打开 0.1 的充值入口
    static final RECHARGE_1_CORNER = "RECHARGE_1_CORNER"
    // 是否需要打开性能监控
    static final ENABLE_MONITOR = "ENABLE_MONITOR"
    // 是否上报分辨率相关项
    static final REPORT_CAMERA_RESOLUTION = "REPORT_CAMERA_RESOLUTION"
    // 是否上报动效视频相关项
    static final REPORT_PERFORMANCE_VIDEO = "REPORT_PERFORMANCE_VIDEO"
    // 是否上报性能相关项，打开PerformanceLog.isPerformanceLogEnabled()此字段才会起作用，上报性能相关项
    static final REPORT_PERFORMANCE = "REPORT_PERFORMANCE"

    // 开发测试环境
    static final URL_MODE_DEV = "URL_MODE_DEV"
    // 预览版环境
    static final URL_MODE_EXPERIENCE = "URL_MODE_EXPERIENCE"
    // 正式环境
    static final URL_MODE_RELEASE = "URL_MODE_RELEASE"

    // 缺省的url环境模式，因为可以去配置，并且有
    static final URL_DEFAULT_MODE = "URL_DEFAULT_MODE"

    @Override
    void apply(Project project) {
        project.extensions.create('ttpicextend', TtpicAppConfigExtension)

        // AppExtension
        def android = project.extensions.getByName("android")
//        if (!(android instanceof AppExtension)) {
//            return
//        }

        project.afterEvaluate {
            def sensitives = project.ttpicextend.sensitives
            // !!!线上的配置，不要去动它!!!
            writeRelease(sensitives, android)
            // debug的情况
            writeDebug(sensitives, android)
        }
        // 注册数据保护
        android.registerTransform(new SafeStringTransform(project))
    }

    private static void writeDebug(def sensitives, Object android) {
        def debug = android.buildTypes.getByName("debug")
        if (debug != null) {
//            debug.buildConfigField("boolean", "DEBUG", "true")
            debug.buildConfigField("boolean", ENABLE_LOG, "true")
            debug.buildConfigField("boolean", TPL_PARSER_DEBUG, "true")
            debug.buildConfigField("boolean", HIDE_APP_TUTORIAL, "false")
            debug.buildConfigField("boolean", RECHARGE_1_CORNER, "false")
            debug.buildConfigField("boolean", ENABLE_MONITOR, "true")
            debug.buildConfigField("boolean", REPORT_CAMERA_RESOLUTION, "true")
            debug.buildConfigField("boolean", REPORT_PERFORMANCE_VIDEO, "false")
            debug.buildConfigField("boolean", REPORT_PERFORMANCE, "true")
            debug.buildConfigField("int", URL_MODE_DEV, "1")
            debug.buildConfigField("int", URL_MODE_EXPERIENCE, "2")
            debug.buildConfigField("int", URL_MODE_RELEASE, "3")
            debug.buildConfigField("int", URL_DEFAULT_MODE, "1")
            stub(sensitives, debug)
        }
    }

    //////////////// don't revise the following code unless you have confirmed that is you need! //////////////////

    private static void writeRelease(def sensitives, Object android) {
        def release = android.buildTypes.getByName("release")
        if (release != null) {
//            release.buildConfigField("boolean", "DEBUG", "false")
            release.buildConfigField("boolean", ENABLE_LOG, "false")
            release.buildConfigField("boolean", TPL_PARSER_DEBUG, "false")
            release.buildConfigField("boolean", HIDE_APP_TUTORIAL, "false")
            release.buildConfigField("boolean", RECHARGE_1_CORNER, "false")
            release.buildConfigField("boolean", ENABLE_MONITOR, "true")
            release.buildConfigField("boolean", REPORT_CAMERA_RESOLUTION, "true")
            release.buildConfigField("boolean", REPORT_PERFORMANCE_VIDEO, "false")
            release.buildConfigField("boolean", REPORT_PERFORMANCE, "true")
            release.buildConfigField("int", URL_MODE_DEV, "1")
            release.buildConfigField("int", URL_MODE_EXPERIENCE, "2")
            release.buildConfigField("int", URL_MODE_RELEASE, "3")
            release.buildConfigField("int", URL_DEFAULT_MODE, "3")
            stub(sensitives, release)
        }
    }

    private static void stub(def sensitives, def type) {
        for (int i = 0; i < sensitives.size(); i += 2) {
            type.buildConfigField("String", sensitives[i], "\"" + sensitives[i+1] + "\"")
        }
    }
}

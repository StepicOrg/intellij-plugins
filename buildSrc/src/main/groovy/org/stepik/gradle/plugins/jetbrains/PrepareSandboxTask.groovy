package org.stepik.gradle.plugins.jetbrains

import com.sun.istack.internal.NotNull
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.*
import org.gradle.internal.FileUtils
import org.gradle.internal.jvm.Jvm
import org.xml.sax.ErrorHandler
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.SAXParseException

@SuppressWarnings("GroovyUnusedDeclaration")
class PrepareSandboxTask extends Copy {
    Object pluginName
    Object pluginJar
    Object configDirectory
    List<Object> librariesToIgnore = []

    PrepareSandboxTask() {
        configurePlugin()
    }

    @InputFile
    File getPluginJar() {
        pluginJar != null ? project.file(pluginJar) : null
    }

    void setPluginJar(Object pluginJar) {
        this.pluginJar = pluginJar
    }

    void pluginJar(Object pluginJar) {
        this.pluginJar = pluginJar
    }

    @Input
    String getPluginName() {
        def pluginName = stringInput(pluginName)
        pluginName != null ? FileUtils.toSafeFileName(pluginName) : null
    }

    static String stringInput(input) {
        input = input instanceof Closure ? (input as Closure).call() : input
        return input?.toString()
    }

    void setPluginName(Object pluginName) {
        this.pluginName = pluginName
    }

    void pluginName(Object pluginName) {
        this.pluginName = pluginName
    }

    @Input
    File getConfigDirectory() {
        configDirectory != null ? project.file(configDirectory) : null
    }

    void setConfigDirectory(File configDirectory) {
        this.configDirectory = configDirectory
    }

    void configDirectory(File configDirectory) {
        this.configDirectory = configDirectory
    }

    @Override
    protected void copy() {
        disableIdeUpdate()
        super.copy()
    }

    private void configurePlugin() {
        CopySpec plugin = mainSpec.addChild().into { "${getPluginName()}/lib" }
        plugin.from {
            def result = [getPluginJar()]

            result
        }
    }

    @NotNull
    static SourceSet mainSourceSet(@NotNull Project project) {
        JavaPluginConvention javaConvention = project.convention.getPlugin(JavaPluginConvention)
        javaConvention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    }


    static Node parseXml(File file) {
        def parser = new XmlParser(false, true, true)
        parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        parser.setErrorHandler(new ErrorHandler() {
            @Override
            void warning(SAXParseException e) throws SAXException {

            }

            @Override
            void error(SAXParseException e) throws SAXException {
                throw e
            }

            @Override
            void fatalError(SAXParseException e) throws SAXException {
                throw e
            }
        })
        InputStream inputStream = new FileInputStream(file)
        InputSource input = new InputSource(new InputStreamReader(inputStream, "UTF-8"))
        input.setEncoding("UTF-8")
        try {
            return parser.parse(input)
        }
        finally {
            inputStream.close()
        }
    }

    @NotNull
    static FileCollection sourcePluginXmlFiles(@NotNull Project project) {
        Set<File> result = new HashSet<>()
        mainSourceSet(project).resources.srcDirs.each {
            def pluginXml = new File(it, "META-INF/plugin.xml")
            if (pluginXml.exists()) {
                try {
                    if (parseXml(pluginXml).name() == 'idea-plugin') {
                        result += pluginXml
                    }
                } catch (SAXParseException ignore) {
                }
            }
        }
        project.files(result)
    }

    private void disableIdeUpdate() {
        def optionsDir = new File(getConfigDirectory(), "options")
        if (!optionsDir.exists() && !optionsDir.mkdirs()) {
            return
        }

        def updatesConfig = new File(optionsDir, "updates.xml")
        if (!updatesConfig.exists() && !updatesConfig.createNewFile()) {
            return
        }
        def parse
        try {
            parse = parseXml(updatesConfig)
        }
        catch (SAXParseException ignore) {
            updatesConfig.text = "<application></application>"
            parse = parseXml(updatesConfig)
        }

        def component = null
        for (Node c : parse.component) {
            if (c.attribute('name') == 'UpdatesConfigurable') {
                component = c
                break
            }
        }
        if (!component) {
            component = new Node(null, 'component', ['name': 'UpdatesConfigurable'])
            parse.append(component)
        }
        def option = null
        for (Node o : component.option) {
            if (o.attribute('name') == 'CHECK_NEEDED') {
                option = o
                break
            }
        }
        if (!option) {
            option = new Node(null, 'option', ['name': 'CHECK_NEEDED'])
            component.append(option)
        }
        option.'@value' = 'false'
        def writer
        try {
            writer = new PrintWriter(new FileWriter(updatesConfig))
            def printer = new XmlNodePrinter(writer)
            printer.preserveWhitespace = true
            printer.print(parse)
        }
        finally {
            if (writer) {
                writer.close()
            }
        }
    }

    @InputFiles
    @Optional
    FileCollection getLibrariesToIgnore() {
        project.files(librariesToIgnore)
    }

    void setLibrariesToIgnore(Object... librariesToIgnore) {
        this.librariesToIgnore.clear()
        this.librariesToIgnore.addAll(librariesToIgnore as List)
    }

    void librariesToIgnore(Object... librariesToIgnore) {
        this.librariesToIgnore.addAll(librariesToIgnore as List)
    }
}

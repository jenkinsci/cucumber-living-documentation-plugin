/*
 * The MIT License
 *
 * Copyright 2016 rmpestano.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.cukedoctor.jenkins;

import com.github.cukedoctor.Cukedoctor;
import com.github.cukedoctor.api.CukedoctorConverter;
import com.github.cukedoctor.api.DocumentAttributes;
import com.github.cukedoctor.api.model.Feature;
import com.github.cukedoctor.extension.CukedoctorExtensionRegistry;
import com.github.cukedoctor.jenkins.model.FormatType;
import com.github.cukedoctor.jenkins.model.TocType;
import com.github.cukedoctor.parser.FeatureParser;
import com.github.cukedoctor.util.Constants;
import com.github.cukedoctor.util.FileUtil;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.ListBoxModel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.*;
import java.util.List;

import static com.github.cukedoctor.util.Assert.hasText;

/**
 * @author rmpestano
 */
public class CukedoctorPublisher extends Recorder {

    private String featuresDir;

    private final boolean numbered;

    private final boolean sectAnchors;

    private final TocType toc;

    private final FormatType format;

    private String title;

    private CukedoctorProjectAction cukedoctorProjectAction;

    @DataBoundConstructor
    public CukedoctorPublisher(String featuresDir, FormatType format, TocType toc, boolean numbered, boolean sectAnchors, String title) {
        this.featuresDir = featuresDir;
        this.numbered = numbered;
        this.toc = toc;
        this.format = format;
        this.sectAnchors = sectAnchors;
        this.title = title;
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        if (cukedoctorProjectAction == null) {
            cukedoctorProjectAction = new CukedoctorProjectAction(project);
        }
        return cukedoctorProjectAction;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
        throws IOException, InterruptedException {

        PrintStream logger = listener.getLogger();
        logger.println("");
        logger.println("Generating living documentation for " + build.getProject().getName() + "...");
        String computedFeaturesDir;
        if (!hasText(featuresDir)) {
            computedFeaturesDir = build.getWorkspace().getRemote();
        } else {
            computedFeaturesDir = new StringBuilder(build.getWorkspace().getRemote()).
                append(System.getProperty("file.separator")).append(featuresDir).
                toString().replaceAll("//", System.getProperty("file.separator"));
        }

        List<Feature> features = FeatureParser.findAndParse(computedFeaturesDir);
        if (!features.isEmpty()) {
            if (!hasText(title)) {
                title = "Living Documentation";
            }

            logger.println("Found " + features.size() + " feature(s).");
            logger.println("Generating living documentation with the following arguments: ");
            logger.println("Features dir: " + computedFeaturesDir);
            logger.println("Format: " + format.getFormat());
            logger.println("Toc: " + toc.getToc());
            logger.println("Title: " + title);
            logger.println("Numbered: " + Boolean.toString(numbered));
            logger.println("Section anchors: " + Boolean.toString(sectAnchors));
            logger.println("");

            File targetBuildDirectory = new File(build.getRootDir(), CukedoctorBaseAction.BASE_URL);
            if (!targetBuildDirectory.exists()) {
                targetBuildDirectory.mkdirs();
            }

            DocumentAttributes documentAttributes = new DocumentAttributes().
                    backend(format.getFormat()).
                    toc(toc.getToc()).
                    numbered(numbered).
                    sectAnchors(sectAnchors).
                    docTitle(title);

            String outputPath = targetBuildDirectory.getAbsolutePath();
            if("all".equals(format.getFormat())){
                File allHtml = new File(outputPath+ System.getProperty("file.separator")+ cukedoctorProjectAction.ALL_DOCUMENTATION);
                if(!allHtml.exists()){
                    allHtml.createNewFile();
                }
                IOUtils.copy(getClass().getResourceAsStream("/"+cukedoctorProjectAction.ALL_DOCUMENTATION), new FileOutputStream(allHtml));
                cukedoctorProjectAction.setDocumentationPage(cukedoctorProjectAction.ALL_DOCUMENTATION);
                //pdf needs to be rendered first otherwise org.jruby.exceptions.RaiseException: (EBADF) Bad file descriptor is thrown
                documentAttributes.backend("pdf");
                execute(features, documentAttributes, outputPath);
                documentAttributes.backend("html5");
                execute(features, documentAttributes, outputPath);
            }else{
                cukedoctorProjectAction.setDocumentationPage("documentation."+format.getFormat());
                execute(features, documentAttributes, outputPath);
            }

            logger.println("Documentation generated successfully!");

        } else {
            logger.println(String.format("No features Found in %s. \nLiving documentation will not be generated.", computedFeaturesDir));

        }

        return true;
    }

    private synchronized void execute(List<Feature> features, DocumentAttributes attrs,String outputPath) {
        Asciidoctor asciidoctor = null;
        try {
            if (attrs.getBackend().equalsIgnoreCase("pdf")) {
                attrs.pdfTheme(true).docInfo(false);
            } else {
                attrs.docInfo(true).pdfTheme(false);
            }
            CukedoctorConverter converter = Cukedoctor.instance(features, attrs);
            String doc = converter.renderDocumentation();
            File adocFile = FileUtil.saveFile(outputPath + "/documentation.adoc", doc);
            asciidoctor = Asciidoctor.Factory.create(CukedoctorPublisher.class.getClassLoader());
            if (attrs.getBackend().equalsIgnoreCase("pdf")) {
                asciidoctor.unregisterAllExtensions();
            } else{
                new CukedoctorExtensionRegistry().register(asciidoctor);
            }
            asciidoctor.convertFile(adocFile, OptionsBuilder.options().backend(attrs.getBackend()).safe(SafeMode.UNSAFE).asMap());
        }finally {
            if(asciidoctor != null){
                asciidoctor.shutdown();
            }
        }
    }


    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }


    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

      /*  public DescriptorImpl() {
             load();
        }*/

        @Override
        public String getHelpFile() {
            return "/plugin/cukedoctor-jenkins/help.html";
        }


        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }


        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Living documentation";
        }

        public ListBoxModel doFillTocItems() {
            ListBoxModel items = new ListBoxModel();
            for (TocType tocType : TocType.values()) {
                items.add(tocType.getToc(), tocType.name());
            }
            return items;
        }

        public ListBoxModel doFillFormatItems() {
            ListBoxModel items = new ListBoxModel();
            for (FormatType formatType : FormatType.values()) {
                items.add(formatType.getFormat(), formatType.name());
            }
            return items;
        }


    }


    public String getFeaturesDir() {
        return featuresDir;
    }

    public boolean isNumbered() {
        return numbered;
    }

    public boolean isSectAnchors() {
        return sectAnchors;
    }

    public TocType getToc() {
        return toc;
    }

    public FormatType getFormat() {
        return format;
    }

    public String getTitle() {
        return title;
    }


}


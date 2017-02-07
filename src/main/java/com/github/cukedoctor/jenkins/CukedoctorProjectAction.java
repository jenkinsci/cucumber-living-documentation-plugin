package com.github.cukedoctor.jenkins;

import static com.github.cukedoctor.util.Assert.notEmpty;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.cukedoctor.util.FileUtil;

import hudson.model.*;

public class CukedoctorProjectAction extends CukedoctorBaseAction implements ProminentProjectAction {

    private static final java.lang.String HTML_DOCUMENTATION = "documentation.html";
    private static final java.lang.String PDF_DOCUMENTATION = "documentation.pdf";
    private final Job<?, ?> job;

    private String jobName;

    public CukedoctorProjectAction(Job<?, ?> job) {
        this.job = job;
    }

    public String job(){
        if(jobName == null){
            jobName = job.getName();
        }
        return jobName;
    }

    /**
     * sidebar panel is visible when html and pdf documentation is available
     * so user don't need to navigate to all.html to choice documentation format
     * @return <code>true</code> if both html and pdf documentation is present on last build <code>false</code> otherwise. 
     */
    public boolean showSidebarPanel() {
        if(documentationPage != null && notEmpty(job.getBuilds())) {
            Run<?, ?> lastBuild = job.getBuilds().getLastBuild();
            final Path BUILD_PATH = Paths.get(lastBuild.getRootDir() + System.getProperty("file.separator") + BASE_URL);

            if(Files.exists(BUILD_PATH)){
                if(!FileUtil.findFiles(BUILD_PATH.toString(),HTML_DOCUMENTATION,true).isEmpty()
                        && !FileUtil.findFiles(BUILD_PATH.toString(),PDF_DOCUMENTATION,true).isEmpty()){
                    return true;
                }
            }
        }
        return false;


    }

    @Override
    protected File dir() {
        Run<?, ?> run = this.job.getLastCompletedBuild();
        if (run != null) {
            File archiveDir = getBuildArchiveDir(run);

            if (archiveDir.exists()) {
                return archiveDir;
            }
        }

        return getProjectArchiveDir();
    }

    private File getProjectArchiveDir() {
        return new File(job.getRootDir(), CukedoctorBaseAction.BASE_URL);
    }

    /** Gets the directory where the HTML report is stored for the given build. */
    private File getBuildArchiveDir(Run<?, ?> run) {
        return new File(run.getRootDir(), CukedoctorBaseAction.BASE_URL);
    }

    @Override
    protected String getTitle() {
        return this.job.getDisplayName();
    }
}
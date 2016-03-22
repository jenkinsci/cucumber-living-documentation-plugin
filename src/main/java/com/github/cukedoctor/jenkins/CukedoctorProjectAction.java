package com.github.cukedoctor.jenkins;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.ProminentProjectAction;
import hudson.model.Run;

public class CukedoctorProjectAction extends CukedoctorBaseAction implements ProminentProjectAction {

    private final AbstractProject<?, ?> project;

    private String jobName;

    public CukedoctorProjectAction(AbstractProject<?, ?> project) {
        this.project = project;
    }

    public String job(){
        if(jobName == null){
            jobName = project.getName();
        }
        return jobName;
    }

    /**
     * sidebar panel is visible when html and pdf documentation is available
     * so user don't need to navigate to all.html to choice documentation format
     */
    public boolean showSidebarPanel() {
        //
        if(documentationPage != null && (documentationPage.equals(ALL_DOCUMENTATION))) {
            for (AbstractBuild<?, ?> build : project.getBuilds()) {
                if (Files.exists(Paths.get(build.getRootDir() + "/" + BASE_URL))) {
                    return true;
                }
            }
        }
        return false;


    }

    @Override
    protected File dir() {
        Run<?, ?> run = this.project.getLastCompletedBuild();
        if (run != null) {
            File archiveDir = getBuildArchiveDir(run);

            if (archiveDir.exists()) {
                return archiveDir;
            }
        }

        return getProjectArchiveDir();
    }

    private File getProjectArchiveDir() {
        return new File(project.getRootDir(), CukedoctorBaseAction.BASE_URL);
    }

    /** Gets the directory where the HTML report is stored for the given build. */
    private File getBuildArchiveDir(Run<?, ?> run) {
        return new File(run.getRootDir(), CukedoctorBaseAction.BASE_URL);
    }

    @Override
    protected String getTitle() {
        return this.project.getDisplayName();
    }
}
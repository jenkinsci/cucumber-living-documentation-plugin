package com.github.cukedoctor.jenkins;

import hudson.model.Job;
import hudson.model.ProminentProjectAction;
import hudson.model.Run;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CukedoctorProjectAction extends CukedoctorBaseAction implements ProminentProjectAction {

    private transient Job<?, ?> job;

    private String jobName;

    public CukedoctorProjectAction(Job<?, ?> job) {
        this.job = job;
    }

    public String job() {
        if (jobName == null) {
            jobName = job.getName();
        }
        return jobName;
    }


    @Override
    protected String getTitle() {
        return this.job.getDisplayName();
    }


    public List<CukedoctorBuildAction> getBuilds() {
        List<CukedoctorBuildAction> builds = new ArrayList<>();

        if(job == null || job.getBuilds() == null) {//will be null after restarts
            return builds;//to do reload builds from disk
        }

        for (Run<?, ?> build : job.getBuilds()) {
            CukedoctorBuildAction action = build.getAction(CukedoctorBuildAction.class);
            if (action != null) {
                builds.add(action);
            }
        }

        return builds;
    }

    @Override
    protected File dir() {
        File dir = null;
        if (job != null && this.job.getLastCompletedBuild() != null) {
            Run<?, ?> run = this.job.getLastCompletedBuild();
            File archiveDir = getBuildArchiveDir(run);
            if (archiveDir.exists()) {
                dir = archiveDir;
            } else {
                dir = getProjectArchiveDir();
            }
        } else {
            dir = getProjectArchiveDir();
        }

        return dir;
    }

    private File getProjectArchiveDir() {
        return new File(job.getRootDir(), CukedoctorBaseAction.BASE_URL);
    }

    /** Gets the directory where docs are stored for the given build. */
    private File getBuildArchiveDir(Run<?, ?> run) {
        return new File(run.getRootDir(), CukedoctorBaseAction.BASE_URL);
    }

}
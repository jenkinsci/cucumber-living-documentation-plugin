package com.github.cukedoctor.jenkins;

import com.github.cukedoctor.config.GlobalConfig;
import hudson.model.Action;
import hudson.model.Run;
import jenkins.tasks.SimpleBuildStep;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CukedoctorBuildAction extends CukedoctorBaseAction implements SimpleBuildStep.LastBuildAction {

    private final Run<?, ?> build;
    private GlobalConfig config;
    private List<CukedoctorProjectAction> projectActions;

    public CukedoctorBuildAction(Run<?, ?> build) {
        config = GlobalConfig.getInstance();
        this.build = build;
        projectActions = new ArrayList<>();
        projectActions.add(new CukedoctorProjectAction(build.getParent()));
     }

    public CukedoctorBuildAction(Run<?, ?> build, GlobalConfig globalConfig) {
        this(build);
        this.config = globalConfig;
     }

    @Override
    protected String getTitle() {
        return this.build.getDisplayName();
    }

    @Override
    protected File dir() {
        return new File(build.getRootDir(), BASE_URL);
    }

    @Override
    public Collection<? extends Action> getProjectActions() {
        return this.projectActions;
    }

    public void addAction(CukedoctorProjectAction action) {
        projectActions.add(action);
    }

    public GlobalConfig getConfig() {
        return config;
    }

}

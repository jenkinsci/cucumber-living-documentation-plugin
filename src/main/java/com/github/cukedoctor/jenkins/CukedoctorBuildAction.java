package com.github.cukedoctor.jenkins;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import hudson.model.Action;
import hudson.model.Run;
import jenkins.tasks.SimpleBuildStep;

public class CukedoctorBuildAction extends CukedoctorBaseAction implements SimpleBuildStep.LastBuildAction {

    private final Run<?, ?> build;
    private List<CukedoctorProjectAction> projectActions;

    public CukedoctorBuildAction(Run<?, ?> build) {
        this.build = build;
        List<CukedoctorProjectAction> projectActions = new ArrayList<>();
        projectActions.add(new CukedoctorProjectAction(build.getParent()));
        this.projectActions = projectActions;
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
}

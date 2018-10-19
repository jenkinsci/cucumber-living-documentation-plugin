package com.github.cukedoctor.jenkins;

import com.github.cukedoctor.jenkins.model.CukedoctorBuild;
import hudson.Extension;
import hudson.model.Action;
import hudson.model.Job;
import hudson.model.ProminentProjectAction;
import hudson.model.Run;
import jenkins.model.TransientActionFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CukedoctorProjectAction extends CukedoctorBaseAction implements ProminentProjectAction {

    private final transient Job<?, ?> job;

    private String jobName;

    private final List<CukedoctorBuild> cukedoctorBuilds;

    public CukedoctorProjectAction(Job<?, ?> job, List<CukedoctorBuild> cukedoctorBuilds) {
        this.job = job;
        this.cukedoctorBuilds = cukedoctorBuilds;
    }

    public String job() {
        if (jobName == null) {
            jobName = job.getName();
        }
        return jobName;
    }


    protected String getTitle() {
        return this.job.getDisplayName();
    }


    public List<CukedoctorBuild> getBuilds() {
        return cukedoctorBuilds;
    }


    @Extension
    public static class CukedoctorActionFactory extends TransientActionFactory<Job<?, ?>> {

        @Override
        public Collection<? extends Action> createFor(Job<?, ?> j) {
            List<CukedoctorBuild> cukedoctorBuilds = new ArrayList<>();

            //collects the list of builds that published living docs to show on the documentation history page
            if (j.getBuilds() != null && !j.getBuilds().isEmpty()) {
                for (Run<?, ?> build : j.getBuilds()) {
                    CukedoctorBuildAction cukedoctorBuildAction = build.getAction(CukedoctorBuildAction.class);
                    if (cukedoctorBuildAction != null) {
                        cukedoctorBuilds.add(cukedoctorBuildAction.getCukedoctorBuild());
                    }
                }
            }
            if (cukedoctorBuilds.isEmpty()) {
                return Collections.emptyList();
            }
            return Collections.singleton(new CukedoctorProjectAction(j, cukedoctorBuilds));
        }

        @Override
        public Class type() {
            return Job.class;
        }
    }


}

package com.github.cukedoctor.jenkins;

import com.github.cukedoctor.jenkins.model.CukedoctorBuild;
import hudson.model.Run;
import jenkins.model.RunAction2;

import java.io.File;

public class CukedoctorBuildAction extends CukedoctorBaseAction implements RunAction2 {

    private transient Run<?, ?> build;
    private final CukedoctorBuild cukedoctorBuild;

    public CukedoctorBuildAction(Run<?, ?> build, CukedoctorBuild cukedoctorBuild) {
        this.build = build;
        this.cukedoctorBuild = cukedoctorBuild;
     }

    @Override
    protected String getTitle() {
        return this.build.getDisplayName();
    }

    private File getDocsPath() {
        return new File(build.getRootDir(), BASE_URL +"/documentation."+(cukedoctorBuild.isHtmlDocs() ? "html":"pdf"));
    }

    public DocsRenderer getDocs() {
        return new DocsRenderer(cukedoctorBuild, getDocsPath(), build.getFullDisplayName());
    }

    public CukedoctorBuild getCukedoctorBuild() {
        return cukedoctorBuild;
    }

    public Run<?, ?> getBuild() {
        return build;
    }


    @Override
    public void onAttached(Run<?, ?> run) {
        build = run;
    }

    @Override
    public void onLoad(Run<?, ?> run) {
        onAttached(run);
    }
}

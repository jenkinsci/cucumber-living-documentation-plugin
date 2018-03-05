package com.github.cukedoctor.jenkins;

import com.github.cukedoctor.jenkins.model.FormatType;
import hudson.model.Action;
import hudson.model.Run;
import jenkins.tasks.SimpleBuildStep;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CukedoctorBuildAction extends CukedoctorBaseAction implements SimpleBuildStep.LastBuildAction {

    private final Run<?, ?> build;
    private FormatType docType;
    private List<CukedoctorProjectAction> projectActions;

    public CukedoctorBuildAction(Run<?, ?> build) {
        docType = FormatType.HTML;
        this.build = build;
        projectActions = new ArrayList<>();
        projectActions.add(new CukedoctorProjectAction(build.getParent()));
     }

    public CukedoctorBuildAction(Run<?, ?> build, FormatType docType) {
        this(build);
        this.docType = docType;
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

    public boolean isHtmlDocs() {
        return FormatType.HTML.equals(docType) || FormatType.ALL.equals(docType);
    }

    public boolean isPdfDocs() {
        return FormatType.PDF.equals(docType) || FormatType.ALL.equals(docType);
    }

    public Run<?, ?> getBuild() {
        return build;
    }
}

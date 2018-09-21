package com.github.cukedoctor.jenkins;

import com.github.cukedoctor.jenkins.model.CukedoctorBuild;
import com.github.cukedoctor.jenkins.model.FormatType;
import hudson.FilePath;
import hudson.model.DirectoryBrowserSupport;
import hudson.model.Run;
import hudson.util.IOUtils;
import jenkins.model.RunAction2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

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
        return new File(build.getRootDir(), BASE_URL + "/documentation" + (cukedoctorBuild.getFormat().equals(FormatType.HTML) ? ".html" : cukedoctorBuild.getFormat().equals(FormatType.PDF) ? ".pdf" : "-all.html"));
    }

    public DocsRenderer getDocs() {
        return new DocsRenderer(getDocsPath(), build.getFullDisplayName());
    }

    public DocsRenderer getDocsHtml() {
        return new DocsRenderer(new File(build.getRootDir(), BASE_URL + "/documentation.html"), build.getFullDisplayName());
    }

    public DocsRenderer getDocsPdf() {
        return new DocsRenderer(new File(build.getRootDir(), BASE_URL + "/documentation.pdf"), build.getFullDisplayName());
    }

    public CukedoctorBuild getCukedoctorBuild() {
        return cukedoctorBuild;
    }

    public Run<?, ?> getBuild() {
        return build;
    }

    public void doDynamic(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {

        File docsPath = getDocsPath();

        if (docsPath.getName().endsWith("all.html")) {
            createAllDocsPage(docsPath);
            DirectoryBrowserSupport dbs = new DirectoryBrowserSupport(this, new FilePath(getDocsPath()), getTitle(), getUrlName(),
                false);

            dbs.generateResponse(req, rsp, this);
        } else if (docsPath.getName().endsWith("html")) {
            rsp.sendRedirect2(req.getContextPath()+"/"+build.getUrl() + BASE_URL + "/docsHtml");
        } else {
            rsp.sendRedirect2(req.getContextPath()+"/"+build.getUrl() +BASE_URL + "/docsPdf");
        }

    }

    @Override
    public void onAttached(Run<?, ?> run) {
        build = run;
    }

    @Override
    public void onLoad(Run<?, ?> run) {
        onAttached(run);
    }

    private void createAllDocsPage(File allDocsPath) {
        if (!allDocsPath.exists()) {
            try (InputStream is = getClass().getResourceAsStream("/" + CukedoctorBaseAction.BUILD_ACTION_ALL_DOCUMENTATION)) {
                IOUtils.copy(is, allDocsPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}

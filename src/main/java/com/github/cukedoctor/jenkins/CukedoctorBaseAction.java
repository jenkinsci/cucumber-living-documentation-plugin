package com.github.cukedoctor.jenkins;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.servlet.ServletException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.FilePath;
import hudson.model.Action;
import hudson.model.DirectoryBrowserSupport;

public abstract class CukedoctorBaseAction implements Action {

    protected static final String ICON_NAME = "/plugin/cucumber-living-documentation/cuke.png";

    protected static final String BASE_URL = "cucumber-living-documentation";

    protected static final String TITLE = "Living documentation";

    private static final String HTML_DOCS = "documentation.html";

    private static final String PDF_DOCS = "documentation.pdf";


    public String getUrlName() {
        return BASE_URL;
    }

    public String getDisplayName() {
        return TITLE;
    }

    public String getIconFileName() {
        return ICON_NAME;
    }

    protected abstract String getTitle();

    protected abstract File dir();


    public void doDynamic(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
        DirectoryBrowserSupport dbs = new DirectoryBrowserSupport(this, new FilePath(dir()), getTitle(), getUrlName(),
                false);

        if (req.hasParameter("doctype")) {
            String docType = req.getParameter("doctype").toLowerCase();
            if (docType.equals("html")) {
                dbs.setIndexFileName(HTML_DOCS);
            } else if (docType.equals("pdf")) {
                dbs.setIndexFileName(PDF_DOCS);
            }
        }

        dbs.generateResponse(req, rsp, this);
    }
}

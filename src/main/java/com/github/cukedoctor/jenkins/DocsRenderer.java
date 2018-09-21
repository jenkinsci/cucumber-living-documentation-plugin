package com.github.cukedoctor.jenkins;

import com.github.cukedoctor.jenkins.model.CukedoctorBuild;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocsRenderer implements Serializable {

    private final CukedoctorBuild cukedoctorBuild;
    private final File docsPath;
    private final String buildName;

    public DocsRenderer(CukedoctorBuild cukedoctorBuild, File docsPath, String buildName) {
        this.cukedoctorBuild = cukedoctorBuild;
        this.docsPath = docsPath;
        this.buildName = buildName;
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    public void doIndex(StaplerRequest request, StaplerResponse response)
            throws IOException, ServletException {
        String fileName = "documentation" + (cukedoctorBuild.isHtmlDocs() ? ".html" : ".pdf");

        try (InputStream is = new FileInputStream(docsPath)){
            response.addHeader("Content-Type",cukedoctorBuild.isHtmlDocs() ?"text/html":"application/pdf");
            response.addHeader("Content-Disposition", "inline; filename=" + fileName);
            response.serveFile(request, is, 0l, 0l, -1l, fileName);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Could not get living documentation for build " + buildName, e);
        }
    }

}

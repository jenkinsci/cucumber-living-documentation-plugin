package com.github.cukedoctor.jenkins;

import hudson.util.IOUtils;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class DocsRenderer implements Serializable {

    private final File docsPath;
    private final String buildName;

    public DocsRenderer(File docsPath, String buildName) {
        this.docsPath = docsPath;
        this.buildName = buildName;
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    public void doIndex(StaplerRequest request, StaplerResponse response)
        throws IOException, ServletException {

        String fileName = docsPath.getName();
        if (request.hasParameter("theme")) {
            final String themeName = request.getParameter("theme")+".css";
            String themeBasePath = docsPath.getAbsolutePath();
            String themePath = new StringBuilder(themeBasePath.substring(0, themeBasePath.lastIndexOf("/") + 1))
                .append("themes/").append(themeName).toString();
            
            String themeContent = new String(Files.readAllBytes(Paths.get(themePath)),"UTF-8");
            String html = new String(Files.readAllBytes(docsPath.toPath()),"UTF-8");
            Document doc = Jsoup.parse(html, "UTF-8");
            Elements head = doc.getElementsByTag("head");
            head.select("style").remove();
            head.append("<style> "+themeContent+"</style>");

            try (InputStream is = new ByteArrayInputStream(doc.toString().getBytes("UTF-8"))) {
                response.addHeader("Content-Type", "text/html");
                response.addHeader("Content-Disposition", "inline; filename=" + fileName);
                response.serveFile(request, is, 0l, 0l, -1l, fileName);
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Could not get living documentation for build " + buildName, e);
            }

        } else {
            if (docsPath.getName().endsWith("all.html")) {
                createAllDocsPage(docsPath);
            }

            try (InputStream is = new FileInputStream(docsPath)) {
                response.addHeader("Content-Type", fileName.endsWith("html") ? "text/html" : "application/pdf");
                response.addHeader("Content-Disposition", "inline; filename=" + fileName);
                response.serveFile(request, is, 0l, 0l, -1l, fileName);
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Could not get living documentation for build " + buildName, e);
            }
        }
    }

    private void createAllDocsPage(File allDocsPath) {
        if (!allDocsPath.exists()) {
            try (InputStream is = getClass().getResourceAsStream("/" + CukedoctorBaseAction.ALL_DOCUMENTATION)) {
                IOUtils.copy(is, allDocsPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}

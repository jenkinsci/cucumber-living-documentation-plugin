package com.github.cukedoctor.jenkins;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.SingleFileSCM;

import com.github.cukedoctor.jenkins.model.FormatType;
import com.github.cukedoctor.jenkins.model.TocType;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.slaves.DumbSlave;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class CucumberLivingDocumentationIT {
	
	private static final String NEW_LINE = System.getProperty("line.separator");
	@Rule
	public JenkinsRule jenkins = new JenkinsRule();

	
	@Test
	public void shouldGenerateLivingDocumentation() throws Exception{
		//given
		FreeStyleProject project = jenkins.createFreeStyleProject("test");
		SingleFileSCM scm = new SingleFileSCM("asciidoctor.json",
				CucumberLivingDocumentationIT.class.getResource("/json-output/asciidoctor/asciidoctor.json").toURI().toURL());
		
		project.setScm(scm);
		CukedoctorPublisher publisher = new CukedoctorPublisher(null, FormatType.HTML, TocType.RIGHT, true, true, "Living Documentation",false,false,false,false,false);
		project.getPublishersList().add(publisher);
		project.save();

		//when
		FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
		
		//then
		jenkins.assertLogContains("Format: html" + NEW_LINE + "Toc: right"+NEW_LINE +
				"Title: Living Documentation"+NEW_LINE+"Numbered: true"+NEW_LINE +
				"Section anchors: true", build);
		jenkins.assertLogContains("Found 4 feature(s)...",build);
		jenkins.assertLogContains("Documentation generated successfully!",build);
        assertTrue(new File(build.getRootDir().getAbsolutePath()+"/cucumber-living-documentation/documentation.html").exists());
    }

    @Test
    public void shouldGenerateLivingDocumentationInPDFFormat() throws Exception{
        //given
        FreeStyleProject project = jenkins.createFreeStyleProject("test");
        SingleFileSCM scm = new SingleFileSCM("asciidoctor.json",
                CucumberLivingDocumentationIT.class.getResource("/json-output/asciidoctor/asciidoctor.json").toURI().toURL());

        project.setScm(scm);
        CukedoctorPublisher publisher = new CukedoctorPublisher(null, FormatType.PDF, TocType.RIGHT, true, true, "Living Documentation",false,false,false,false,false);
        project.getPublishersList().add(publisher);
        project.save();

        //when
        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);

        //then
        jenkins.assertLogContains("Format: pdf" + NEW_LINE + "Toc: right"+NEW_LINE +
                "Title: Living Documentation"+NEW_LINE+"Numbered: true"+NEW_LINE +
                "Section anchors: true", build);
        jenkins.assertLogContains("Found 4 feature(s)...",build);
        jenkins.assertLogContains("Documentation generated successfully!",build);
        assertTrue(new File(build.getRootDir().getAbsolutePath()+"/cucumber-living-documentation/documentation.pdf").exists());
    }

    @Test
    public void shouldGenerateLivingDocumentationInHtmlAndPDFFormat() throws Exception{
        //given
        FreeStyleProject project = jenkins.createFreeStyleProject("test");
        SingleFileSCM scm = new SingleFileSCM("asciidoctor.json",
                CucumberLivingDocumentationIT.class.getResource("/json-output/asciidoctor/asciidoctor.json").toURI().toURL());

        project.setScm(scm);
        CukedoctorPublisher publisher = new CukedoctorPublisher(null, FormatType.ALL, TocType.RIGHT, true, true, "Living Documentation",false,false,false,false,false);
        project.getPublishersList().add(publisher);
        project.save();

        //when
        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);

        //then
        jenkins.assertLogContains("Format: all" + NEW_LINE + "Toc: right"+NEW_LINE +
                "Title: Living Documentation"+NEW_LINE+"Numbered: true"+NEW_LINE +
                "Section anchors: true", build);
        jenkins.assertLogContains("Found 4 feature(s)...",build);
        jenkins.assertLogContains("Documentation generated successfully!",build);
        assertTrue(new File(build.getRootDir().getAbsolutePath()+"/cucumber-living-documentation/documentation.pdf").exists());
        assertTrue(new File(build.getRootDir().getAbsolutePath()+"/cucumber-living-documentation/documentation.html").exists());
    }
	
	@Test
	public void shouldGenerateLivingDocumentationOnSlaveNode() throws Exception{
		DumbSlave slave = jenkins.createOnlineSlave();
		FreeStyleProject project = jenkins.createFreeStyleProject("test");
		project.setAssignedNode(slave);
		
		SingleFileSCM scm = new SingleFileSCM("asciidoctor.json",
				CucumberLivingDocumentationIT.class.getResource("/json-output/asciidoctor/asciidoctor.json").toURI().toURL());
		
		project.setScm(scm);
		CukedoctorPublisher publisher = new CukedoctorPublisher(null, FormatType.HTML, TocType.RIGHT, true, true, "Living Documentation",false,false,false,false,false);
		project.getPublishersList().add(publisher);
		project.save();

		FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
		jenkins.assertLogContains("Format: html" + NEW_LINE + "Toc: right"+NEW_LINE +
				"Title: Living Documentation"+NEW_LINE+"Numbered: true"+NEW_LINE +
				"Section anchors: true", build);
		jenkins.assertLogContains("Found 4 feature(s)...",build);
		jenkins.assertLogContains("Documentation generated successfully!",build);
		Assert.assertTrue("It should run on slave",build.getBuiltOn().equals(slave));
        assertTrue(new File(build.getRootDir().getAbsolutePath()+"/cucumber-living-documentation/documentation.html").exists());
	}
}

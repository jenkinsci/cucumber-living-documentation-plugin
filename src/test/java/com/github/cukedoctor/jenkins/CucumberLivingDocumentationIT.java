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

public class CucumberLivingDocumentationIT {
	
	private static final String NEW_LINE = System.getProperty("line.separator");
	@Rule
	public JenkinsRule jenkins = new JenkinsRule();
	
	
	
	@Test
	public void shouldGenerateLivingDocumentatation() throws Exception{
		//given
		System.setProperty("hudson.model.DirectoryBrowserSupport.CSP","");
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
		
	}
	
	@Test
	public void shouldGenerateLivingDocumentatationOnSlaveNode() throws Exception{
		System.setProperty("hudson.model.DirectoryBrowserSupport.CSP","");
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
		
	}
}

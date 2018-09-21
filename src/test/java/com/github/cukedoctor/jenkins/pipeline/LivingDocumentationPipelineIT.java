package com.github.cukedoctor.jenkins.pipeline;

import hudson.model.Result;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.Arrays;

public class LivingDocumentationPipelineIT {

    @Rule
    public JenkinsRule j = new JenkinsRule();


    @Test
    public void shouldNotPublishLivingDocumentationWhenNoFeaturesAreFound() throws Exception {
        WorkflowJob job = j.jenkins.createProject(WorkflowJob.class, "living-documentation");
        job.setDefinition(new CpsFlowDefinition(StringUtils.join(Arrays.asList(
                "node {",
                "    step([$class: 'CukedoctorPublisher', featuresDir: '', format: 'HTML', hideFeaturesSection: false, hideScenarioKeyword: false, hideStepTime: false, hideSummary: false, hideTags: false, numbered: true, sectAnchors: true, title: 'Living Documentation', toc: 'RIGHT'])",
                "}"), "\n"),true));
        WorkflowRun run = j.assertBuildStatusSuccess(job.scheduleBuild2(0).get());
        j.assertLogContains("No features Found", run);
    }

    @Test
    public void shouldNotPublishLivingDocumentationWithoutRelaxedContentSecurityPolicy() throws Exception {
        WorkflowJob job = j.jenkins.createProject(WorkflowJob.class, "living-documentation");
        job.setDefinition(new CpsFlowDefinition(StringUtils.join(Arrays.asList(
                "node {",
                "svn 'https://subversion.assembla.com/svn/cucumber-json-files/trunk'",
                "    step([$class: 'CukedoctorPublisher', featuresDir: '', format: 'HTML', hideFeaturesSection: false, hideScenarioKeyword: false, hideStepTime: false, hideSummary: false, hideTags: false, numbered: true, sectAnchors: true, title: 'Living Documentation', toc: 'RIGHT'])",
                "}"), "\n"),true));
        WorkflowRun run = j.assertBuildStatus(Result.SUCCESS,job.scheduleBuild2(0).get());
        j.assertLogContains("ERROR: To use Living Documentation plugin you need to relax content security policy", run);
    }


    @Test
    public void shouldPublishLivingDocumentationViaPipeline() throws Exception {
        WorkflowJob job = j.jenkins.createProject(WorkflowJob.class, "living-documentation");
        job.setDefinition(new CpsFlowDefinition(StringUtils.join(Arrays.asList(
                "node {",
                "svn 'https://subversion.assembla.com/svn/cucumber-json-files/trunk'",
                "    step([$class: 'CukedoctorPublisher', featuresDir: 'cukedoctor', format: 'HTML', hideFeaturesSection: false, hideScenarioKeyword: false, hideStepTime: false, hideSummary: false, hideTags: false, numbered: true, sectAnchors: true, title: 'Living Documentation', toc: 'RIGHT'])",
                "}"), "\n"),true));
        WorkflowRun run = j.assertBuildStatusSuccess(job.scheduleBuild2(0).get());
        j.assertLogContains("Found 4 feature(s)...", run);
        j.assertLogContains("Documentation generated successfully!", run);
    }

    @Test
    public void shouldPublishLivingDocumentationInPDFFormat() throws Exception {
        WorkflowJob job = j.jenkins.createProject(WorkflowJob.class, "living-documentation");
        job.setDefinition(new CpsFlowDefinition(StringUtils.join(Arrays.asList(
                "node {",
                "svn 'https://subversion.assembla.com/svn/cucumber-json-files/trunk'",
                "    step([$class: 'CukedoctorPublisher', featuresDir: 'cukedoctor', format: 'PDF', hideFeaturesSection: false, hideScenarioKeyword: false, hideStepTime: false, hideSummary: false, hideTags: false, numbered: true, sectAnchors: true, title: 'Living Documentation', toc: 'RIGHT'])",
                "}"), "\n"),true));
        WorkflowRun run = j.assertBuildStatusSuccess(job.scheduleBuild2(0).get());
        j.assertLogContains("Found 4 feature(s)...", run);
        j.assertLogContains("Documentation generated successfully!", run);
    }


}

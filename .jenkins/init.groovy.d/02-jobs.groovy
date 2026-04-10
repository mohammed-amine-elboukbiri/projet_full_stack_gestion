import hudson.plugins.git.BranchSpec
import hudson.plugins.git.UserRemoteConfig
import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import hudson.plugins.git.GitSCM

def instance = Jenkins.get()

def createPipelineJob = { String jobName, String scriptPath ->
    def existing = instance.getItem(jobName)
    if (existing != null) {
        return
    }

    def job = instance.createProject(WorkflowJob, jobName)
    def scm = new GitSCM(
        [new UserRemoteConfig("https://github.com/mohammed-amine-elboukbiri/Application-Full-Stack-de-Gestion-de-Projets.git", null, null, null)],
        [new BranchSpec("*/main")],
        false,
        [],
        null,
        null,
        []
    )
    def definition = new CpsScmFlowDefinition(scm, scriptPath)
    definition.setLightweight(true)
    job.setDefinition(definition)
    job.save()
}

createPipelineJob("project-management-ci", "Jenkinsfile")
createPipelineJob("project-management-cd", ".jenkins/Jenkinsfile.cd")

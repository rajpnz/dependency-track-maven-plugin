package io.github.pmckeown.dependencytrack.project;

import java.util.ArrayList;
import java.util.List;

public class ProjectListBuilder {

    private List<Project> projects = new ArrayList<>();

    public static ProjectListBuilder aListOfProjects() {
        return new ProjectListBuilder();
    }

    public ProjectListBuilder withProject(ProjectBuilder projectBuilder) {
        this.projects.add(projectBuilder.build());
        return this;
    }

    public List<Project> build() {
        return this.projects;
    }
}

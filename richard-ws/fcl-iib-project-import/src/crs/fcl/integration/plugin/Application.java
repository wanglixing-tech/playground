package crs.fcl.integration.plugin;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;

public class Application implements IApplication {

	public Object start(IApplicationContext context) throws Exception {
		getLog().log(new Status(IStatus.OK, "crs.fcl.integration.plugin", "Starting org.eclipse.core.runtime bundle..."));
		String[] args = (String[]) context.getArguments().get(IApplicationContext.APPLICATION_ARGS);

		boolean build = false;

		// Determine projects to import
		List<String> projects = new LinkedList<String>();
		for (int i = 0; i < args.length; ++i) {
			if ("-import".equals(args[i]) && i + 1 < args.length) {
				projects.add(args[++i]);
			} else if ("-build".equals(args[i])) {
				build = true;
			}
		}

		if (projects.size() == 0) {
			System.out.println("No projects to import!");
		} else {
			for (String projectPath : projects) {
				getLog().log(new Status(IStatus.OK, "crs.fcl.integration.plugin", "Importing Project from:" + projectPath));
				System.out.println("Importing project from: " + projectPath);

				// Import project description:
				IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(
						new Path(projectPath).append(".project"));
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
				if (! project.exists()) {
					project.create(description, null);
					project.open(null);
					ResourcesPlugin.getWorkspace().save(true, null);
				} else {
					getLog().log(new Status(IStatus.OK, "crs.fcl.integration.plugin", "Project:" + projectPath + "has already been there, keep it."));					
					System.out.println("Project:" + projectPath + "has already been there, keep it.");
				}
			}

			// Build all projects after importing
			if (build) {
				ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
				ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
			}
		}
		return null;
	}

	public void stop() {
		getLog().log(new Status(IStatus.OK, "crs.fcl.integration.plugin", "Stoping org.eclipse.core.runtime bundle..."));
	}
	
	public static ILog getLog() {
	    return Platform.getLog(Platform.getBundle("crs.fcl.integration.plugin"));
	}

}

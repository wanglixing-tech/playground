package crs.fcl.integration.plugin;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class Application implements IApplication {

	public Object start(IApplicationContext context) throws Exception {
		getLog().log(new Status(IStatus.OK, "toolkit-plugin", "Starting org.eclipse.core.runtime bundle..."));

		String[] args = (String[]) context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
		// Get projects with full path for importing, can be more than one projects, 
		// but need to specify -import XXXXXXXXfor each one
		List<String> projectList = new LinkedList<String>();		
		for (int i = 0; i < args.length; ++i) {
			if ("-import".equals(args[i]) && i + 1 < args.length) {
				projectList.add(args[++i]);
			}
		}
		
		if (projectList.size() == 0) {
			org.eclipse.core.runtime.Platform.getLog(null).log(new Status(IStatus.OK, "tool-plugin", "No Eclipse project specified from command-line with '-import'"));
		} else {
			for (String projectPath : projectList) {
				getLog().log(new Status(IStatus.OK, "toolkit-plugin", "Importing Project:" + projectPath));
				// Import project description:
				IProjectDescription description = ResourcesPlugin.getWorkspace()
						.loadProjectDescription(new Path(projectPath).append(".project"));
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
				if (! project.exists()) {
					project.create(description, null);
					project.open(null);
					ResourcesPlugin.getWorkspace().save(true, null);
				} else {
					getLog().log(new Status(IStatus.OK, "toolkit-plugin", "Project:" + projectPath + "has already been there, keep it."));					
				}
			}
		}
		return null;
	}

	public void stop() {
		getLog().log(new Status(IStatus.OK, "toolkit-plugin", "Stoping org.eclipse.core.runtime bundle..."));
	}
	
	public static ILog getLog() {
	    return Platform.getLog(Platform.getBundle("toolkit-plugin"));
	}
}

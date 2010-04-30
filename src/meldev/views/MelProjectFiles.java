package meldev.views;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class MelProjectFiles {

	//! Get a list of all the MEL files in all open projects
	public static ArrayList<IFile> get() {
		ArrayList<IFile> files = new ArrayList<IFile>();
		try {
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			ArrayList<IResource> reslist = new ArrayList<IResource>();
			for(IResource res: projects) {
				reslist.add(res);
			}
			for (int i=0; i < reslist.size(); ++i) {
				IResource res = reslist.get(i);
				if (res instanceof IContainer) {
					for (IResource member : ((IContainer)res).members()) {
						reslist.add(member);
					}
				} else if (res instanceof IFile) {
					//System.err.print(res.getName() + "\n");
					if (res.getFileExtension().equals("mel")) {
						files.add((IFile)res);
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return files;
	}
}

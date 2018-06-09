package ca.footeware.logstash.contentproviders;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

public class FileTreeContentProvider implements ITreeContentProvider {

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    	
    }

    public Object[] getElements(Object inputElement) {
    	return ResourcesPlugin.getWorkspace().getRoot().getProjects();
    }

    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof IProject) {
            IProject projects = (IProject) parentElement;
            try {
                return projects.members();
            } catch (CoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (parentElement instanceof IFolder) {
            IFolder ifolder = (IFolder) parentElement;
            try {
                return ifolder.members();
            } catch (CoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    public Object getParent(Object element) {
        if (element instanceof IProject) {
            IProject projects = (IProject) element;
            return projects.getParent();
        }
        if (element instanceof IFolder) {
            IFolder folder = (IFolder) element;
            return folder.getParent();
        }
        if (element instanceof IFile) {
            IFile file = (IFile) element;
            return file.getParent();
        }
        return null;
    }

    public boolean hasChildren(Object element) {
        if (element instanceof IProject) {
            IProject projects = (IProject) element;
            try {
                return projects.members().length > 0;
            } catch (CoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (element instanceof IFolder) {
            IFolder folder = (IFolder) element;
            try {
                return folder.members().length > 0;
            } catch (CoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }

}
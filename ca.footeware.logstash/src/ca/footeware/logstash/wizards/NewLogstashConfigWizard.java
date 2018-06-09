/**
 * 
 */
package ca.footeware.logstash.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Footeware.ca
 *
 */
public class NewLogstashConfigWizard extends Wizard implements INewWizard {

	protected LogstashWizardPage pageOne;
	private IStructuredSelection selection;

	/**
	 * Constructor.
	 */
	public NewLogstashConfigWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("New Logstash Configuration");

		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
		// use the org.eclipse.core.runtime.Path as import
		URL url = FileLocator.find(bundle, new Path("icons/logstash-wizard.png"), null);
		// get an imageDescriptor and create Image object
		ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);
		setDefaultPageImageDescriptor(imageDescriptor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		pageOne = new LogstashWizardPage(selection);
		addPage(pageOne);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		IProject parent = pageOne.getParent();
		String name = pageOne.getName();
		String port = pageOne.getPort();
		String host = pageOne.getHost();
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IProject project = root.getProject(parent.getName());
        IFolder folder = project.getFolder(name);
        try {
			folder.create(true, true, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
        
        IFile jvmOptions = folder.getFile("jvm.options");
        InputStream source = new ByteArrayInputStream("bob".getBytes());
        try {
			jvmOptions.create(source, true, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return true;
	}

}

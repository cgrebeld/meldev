package meldev.views;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;


/**
 * 
 */

/**
 * @author chris
 *
 */
public class MelSearchPage extends DialogPage implements ISearchPage {

	private ISearchPageContainer fContainer;
	private Text termText;

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchPage#performAction()
	 */
	@Override
	public boolean performAction() {
        Object[] workingSet = new Object[] {
                ResourcesPlugin.getWorkspace().getRoot()};

        NewSearchUI.activateSearchResultView();
        try {
            fContainer.getRunnableContext().run(
                false, 
                true, 
                new MelSearchOperation(
                    termText.getText(), 
                    workingSet));
                
            return true;
        }
        catch(InterruptedException ex) {
            // ignore
        }
                
        return false;

	}
	
	 public void setVisible(boolean visible) {
	        super.setVisible(visible);
	        if(visible)
	            termText.setFocus();
	 }


	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchPage#setContainer(org.eclipse.search.ui.ISearchPageContainer)
	 */
	@Override
	public void setContainer(ISearchPageContainer container) {
		fContainer = container;

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		  Composite composite = new Composite(parent, SWT.NONE);
	        composite.setLayoutData(
	            new GridData(
	                GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
	        composite.setLayout(new GridLayout());
	        
	        Label label = new Label(composite, SWT.SINGLE);
	        label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
	        label.setText("Search string:");

	        Composite termGroup = new Composite(composite, SWT.NONE);
	        termGroup.setLayoutData(
	            new GridData(
	                GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
	        termGroup.setLayout(new GridLayout(2, false));
	        
	        termText = new Text(termGroup, SWT.SINGLE | SWT.BORDER);
	        termText.setLayoutData(
	            new GridData(
	                GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
	        termText.addModifyListener(new ModifyListener() {
	            public void modifyText(ModifyEvent e) {
	            	if (termText.getText().trim().length() > 0) {
		            	fContainer.setPerformActionEnabled(true);
	            	}
	            }
	        });


	}

}

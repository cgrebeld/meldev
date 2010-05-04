package meldev.editors;

import meldev.Activator;
import meldev.preferences.PreferenceConstants;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.HippieProposalProcessor;

public class MelSourceViewerConfiguration extends TextSourceViewerConfiguration {
	private MelScanner fCodeScanner;
	private RuleBasedScanner fSingleLineScanner;
	private RuleBasedScanner fMultiLineScanner;
	private RuleBasedScanner fStringScanner;

	public MelSourceViewerConfiguration() {
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return MelPartitionScanner.fContentTypes;
	}

	protected MelScanner getCodeScanner() {
		if (fCodeScanner == null) {
			fCodeScanner = new MelScanner();
			fCodeScanner.setDefaultReturnToken(
			new Token(
				new TextAttribute(new Color(Display.getDefault(), 
						PreferenceConverter.getColor(Activator.getDefault().getPreferenceStore(),
								PreferenceConstants.P_SYNTAXCOLOR_DEFAULT))))
				);
		}
		return fCodeScanner;
	}
	
	protected RuleBasedScanner getSingleLineCommentScanner() {
		if (fSingleLineScanner == null) {
			fSingleLineScanner = new RuleBasedScanner();
			fSingleLineScanner.setDefaultReturnToken(new Token(
					new TextAttribute(new Color(Display.getDefault(),
							PreferenceConverter.getColor(Activator.getDefault().getPreferenceStore(),
									PreferenceConstants.P_SYNTAXCOLOR_COMMENT)))));
		}
		return fSingleLineScanner;
	}
	protected RuleBasedScanner getMultiLineCommentScanner() {
		if (fMultiLineScanner == null) {
			fMultiLineScanner = new RuleBasedScanner();
			fMultiLineScanner.setDefaultReturnToken(new Token(
					new TextAttribute(new Color(Display.getDefault(),
							PreferenceConverter.getColor(Activator.getDefault().getPreferenceStore(),
									PreferenceConstants.P_SYNTAXCOLOR_COMMENT)))));
		}
		return fMultiLineScanner;
	}
	protected RuleBasedScanner getStringScanner() {
		if (fStringScanner == null) {
			fStringScanner = new RuleBasedScanner();
			fStringScanner.setDefaultReturnToken(new Token(
					new TextAttribute(new Color(Display.getDefault(),
							PreferenceConverter.getColor(Activator.getDefault().getPreferenceStore(),
									PreferenceConstants.P_SYNTAXCOLOR_STRING)))));
		}
		return fStringScanner;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getContentAssistant(org.eclipse.jface.text.source.ISourceViewer)
	 */

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant = new ContentAssistant();
		assistant.setContentAssistProcessor(new MelContentAssistProcessor(), IDocument.DEFAULT_CONTENT_TYPE);
		return assistant;
	}

	protected void resetMelScanner() {
		if (fCodeScanner != null) {
			fCodeScanner.reset();
		}
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new DefaultDamagerRepairer(getMultiLineCommentScanner());
		reconciler.setDamager(dr, MelPartitionScanner.MULTILINE_COMMENT);
		reconciler.setRepairer(dr, MelPartitionScanner.MULTILINE_COMMENT);

		dr = new DefaultDamagerRepairer(getSingleLineCommentScanner());
		reconciler.setDamager(dr, MelPartitionScanner.SINGLELINE_COMMENT);
		reconciler.setRepairer(dr, MelPartitionScanner.SINGLELINE_COMMENT);

		dr = new DefaultDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, MelPartitionScanner.STRING);
		reconciler.setRepairer(dr, MelPartitionScanner.STRING);
		
		return reconciler;
	}
	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(
			ISourceViewer sourceViewer, String contentType) {
		IAutoEditStrategy[] strats = { new MelAutoEditStrategy(getIndentPrefixes(sourceViewer, contentType)) }; 
		return strats;
	}

}
package meldev.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import meldev.editors.MelEditor;
import meldev.editors.MelTag;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.texteditor.IDocumentProvider;

/*! This is a really embarrassing way to parse the code
  Ideally we would use dltk to set up a real parser, but don't see the need right now
  */ 
public class MelDocumentTagsContentProvider implements ITreeContentProvider {
		private ArrayList<MelTag> fTags;  // the list of tags, in increasing offset order
		private IDocumentListener fDocListener;
		private IDocument fDocument;
//		private Pattern fProcPat = Pattern.compile("(global\\s+)?proc[\\s]+([int|string|float|void]\\s+)?([a-zA-Z_][a-zA-Z_0-9]*)\\s*\\(");
		// group 3 is proc name, group 1 is 'global' or empty
		//(global\s+)?proc\s+([int|string|float|void]+[\s\[\]]*\s)*([a-zA-Z_][a-zA-Z_0-9]*)[\s]*\("
		private Pattern fProcPat = Pattern.compile("(global\\s+)?proc\\s+((int|string|float|void)[\\s\\[\\]]*\\s)*([a-zA-Z_][a-zA-Z_0-9]*)[\\s]*\\(");
		private Pattern fVarDeclPat = Pattern.compile("(global\\s+)?(int|string|float)\\s+(\\$[a-zA-Z_0-9]+[\\[\\]]*)");
		protected ITypedRegion fNextChangeRegion;
		private IDocumentProvider fDocumentProvider;
		private ListenerList fChangeListeners = new ListenerList();
		private MelEditor fEditor;
		
		static private ArrayList<MelDocumentTagsContentProvider> fProviderInstances;
		static {
			fProviderInstances = new ArrayList<MelDocumentTagsContentProvider>();
		}
		//! get the list of all instances of this class
		static ArrayList<MelDocumentTagsContentProvider> getProviderInstances() {
			return fProviderInstances;
		}
		
		public MelEditor getEditor() {
			return fEditor;
		}

		public interface ITagsChangeListener {
			public void tagsChanged();
		};
		
		public MelDocumentTagsContentProvider(IDocumentProvider documentProvider, MelEditor editor) {
			super();
			fDocumentProvider = documentProvider;
			fEditor = editor;
			fTags = new ArrayList<MelTag>();
			fProviderInstances.add(this);
		}
		public void addChangeListener(ITagsChangeListener obj) {
			fChangeListeners.add(obj);
		}
		@Override
		public Object[] getChildren(Object parentElement) {
			return null;
		}
		@Override
		public Object getParent(Object element) {
			return null;
		}
		@Override
		public boolean hasChildren(Object element) {
			return false;
		}
		@Override
		public Object[] getElements(Object inputElement) {
			return fTags.toArray();
		}
		@Override
		public void dispose() {
			if (fDocListener != null && fDocument != null) {
				fDocument.removeDocumentListener(fDocListener);
				fDocListener = null;
			}
			fProviderInstances.remove(this);
		}
		
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			/*
			 * MessageDialog.openInformation(
			 * getTreeViewer().getControl().getShell(), "Mel Explorer",
			 * "ViewContentProvider inputChanged!");
			 */
			IDocument newdoc = fDocumentProvider.getDocument(newInput);
			if (newdoc != null) {
				if (fDocListener != null) {
					fDocument.removeDocumentListener(fDocListener);
					fDocListener = null;
				}
				fDocument = newdoc;
				fDocListener = new IDocumentListener() {
					@Override
					public void documentChanged(DocumentEvent event) {
						// TODO: Can we do incremental parsing so we don't have to repopulate the whole tree?
						//       is this even an issue?
						parse(fDocument);
						for(Object listener : fChangeListeners.getListeners()) {
							((ITagsChangeListener)listener).tagsChanged();
						}
					}
					@Override
					public void documentAboutToBeChanged(DocumentEvent event) {
					}
				};
				fDocument.addDocumentListener(fDocListener);
				parse(fDocument);
			}
		}
		// Parse the given document
		public void parse(IDocument document) {
			fTags.clear();
			
			// Find all the global scopes
			String mel = document.get();
			int ix = 0;
			int max = mel.length() - 1;
			int scopelevel = 0;
			ArrayList<Integer> scopes = new ArrayList<Integer>();
			scopes.add(0);
			while ( ix <= max ) {
				char ch = mel.charAt(ix);
				if (ch == '{' || ch == '(') {
					//System.err.printf("up we go %d\n", ix);
					scopelevel +=1;
					if (scopelevel == 1)
						scopes.add(ix-1);
				}
				else if (ch == '}' || ch == ')') {
					scopelevel -=1;
					//System.err.printf("down we go %d\n", ix);
					if (scopelevel == 0)
						scopes.add(ix);
				}
				ix +=1;
			}
			if (scopelevel == 0)
				scopes.add(max);

			// find all the tags in the code regions
			ITypedRegion[] regions = document.getDocumentPartitioner().computePartitioning(0, document.getLength());
			for( ITypedRegion region : regions) {
				if (region.getType().equals(IDocument.DEFAULT_CONTENT_TYPE)) {
					try {
						mel = document.get(region.getOffset(), region.getLength());
						
						// find procedure definitions
						Matcher matcher = fProcPat.matcher(mel.subSequence(0,
								mel.length() - 1));
						while (matcher.find()) {
							MelTag tag = new MelTag();
							tag.offset = matcher.start(4) + region.getOffset();
							tag.length = matcher.end(4) - matcher.start(4);
							tag.tag = matcher.group(4);
							tag.isGlobal = matcher.group(1) != null;
							tag.type = MelTag.Type.kProcDef;
							fTags.add(tag);
						}
						matcher = fVarDeclPat.matcher(mel.subSequence(0, mel.length()-1));
						while (matcher.find()) {
							MelTag tag = new MelTag();
							tag.offset = matcher.start(3) + region.getOffset();
							tag.length = matcher.end(3) - matcher.start(3);
							// only add this tag if it's inside a global scope
							for (ix = 0; ix < scopes.size()-1; ix+=2) {
								int st = scopes.get(ix);
								int end = scopes.get(ix+1);
								if (tag.offset > st && tag.offset < end) {
									tag.tag = matcher.group(3);
									tag.isGlobal = matcher.group(1) != null;
									tag.type = MelTag.Type.kVarDef;
									fTags.add(tag);
									break;
								}
							}
						}
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
			}
			// sort in file order
			Collections.sort(fTags);
		}
		public Object[] getTagAt(int offset) {
			MelTag activeTag = null;
			for(MelTag tag : fTags) {
				if (offset < tag.offset) {
					activeTag = tag;
				}
			}
			Object parent = getParent(activeTag);
			if (null != parent) {
				return new Object [] { parent, activeTag };
			} else {
				return new Object [] {activeTag };
			}
		}
	}

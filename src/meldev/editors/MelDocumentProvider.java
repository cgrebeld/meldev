package meldev.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

public class MelDocumentProvider extends TextFileDocumentProvider {
	@Override
	public IDocument getDocument(Object element) {
		//TODO: Why does this return null for ext file?
		IDocument document = super.getDocument(element);
		if (document != null && document.getDocumentPartitioner() == null) {
			MelPartitionScanner scanner = new MelPartitionScanner();
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					scanner,
					MelPartitionScanner.fContentTypes );
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		} 
		return document;
	}
}

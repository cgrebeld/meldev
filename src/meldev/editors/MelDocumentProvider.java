package meldev.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

public class MelDocumentProvider extends FileDocumentProvider {

	protected IDocument createDocument(Object element) throws CoreException {
		//TODO: Why does this return null for ext file?
		IDocument document = super.createDocument(element);
		if (document != null) {
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
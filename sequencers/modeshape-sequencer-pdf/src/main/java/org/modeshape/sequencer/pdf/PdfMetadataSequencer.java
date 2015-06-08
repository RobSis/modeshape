/*
 * ModeShape (http://www.modeshape.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.modeshape.sequencer.pdf;

import java.io.IOException;
import java.util.Calendar;

import javax.jcr.Binary;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import static org.modeshape.sequencer.pdf.PdfMetadataLexicon.METADATA_NODE;
import static org.modeshape.sequencer.pdf.PdfMetadataLexicon.AUTHOR;
import static org.modeshape.sequencer.pdf.PdfMetadataLexicon.CREATOR;
import static org.modeshape.sequencer.pdf.PdfMetadataLexicon.CREATION_DATE;
import static org.modeshape.sequencer.pdf.PdfMetadataLexicon.PRODUCER;
import static org.modeshape.sequencer.pdf.PdfMetadataLexicon.TITLE;
import static org.modeshape.sequencer.pdf.PdfMetadataLexicon.PAGES;
import static org.modeshape.sequencer.pdf.PdfMetadataLexicon.MODIFICATION_DATE;
import static org.modeshape.sequencer.pdf.PdfMetadataLexicon.KEYWORDS;
import static org.modeshape.sequencer.pdf.PdfMetadataLexicon.SUBJECT;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.modeshape.common.util.CheckArg;
import org.modeshape.common.util.StringUtil;
import org.modeshape.jcr.api.JcrConstants;
import org.modeshape.jcr.api.nodetype.NodeTypeManager;
import org.modeshape.jcr.api.sequencer.Sequencer;

/**
 * A sequencer that processes the binary content of PDF file, extracts the metadata for the file,
 * and then writes that metadata to the repository.
 * <p>
 * This sequencer produces data that corresponds to the following structure:
 * <ul>
 * <li><strong>pdf:metadata</strong> node of type <code>pdf:metadata</code>
 * <ul>
 * <li><strong>jcr:mimeType</strong> - optional string property for the mime type of the file</li>
 * <li><strong>pdf:author (string)</strong> - optional string property for the author of the file</li>
 * <li><strong>pdf:creation_date (date)</strong> - optional date property for the creation date of the file</li>
 * <li><strong>pdf:creator (string)</strong> - optional string property for the creator of the file</li>
 * <li><strong>pdf:keywords (string)</strong> - optional string property for the keywords of the file</li>
 * <li><strong>pdf:modification_date (date)</strong> - optional date property for the modification date of the file</li>
 * <li><strong>pdf:producer (string)</strong> - optional string property for the producer of the file</li>
 * <li><strong>pdf:subject (string)</strong> - optional string property for the subject of the file</li>
 * <li><strong>pdf:title (string)</strong> - optional string property for the title of the file</li>
 * <li><strong>pdf:pages (long)</strong> - optional long property for the number of pages in the file</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 */
public class PdfMetadataSequencer extends Sequencer {

    public static final class MimeTypeConstants {
        public static final String PDF = "application/pdf";
        public static final String PDF_EXPERIMENTAL = "application/x-pdf";
    }

    @Override
    public void initialize( NamespaceRegistry registry,
                            NodeTypeManager nodeTypeManager ) throws RepositoryException, IOException {
        super.registerNodeTypes("pdf.cnd", nodeTypeManager, true);
        registerDefaultMimeTypes(MimeTypeConstants.PDF, MimeTypeConstants.PDF_EXPERIMENTAL);
    }

    @Override
    public boolean execute( Property inputProperty,
                            Node outputNode,
                            Context context ) throws Exception {
        Binary binaryValue = inputProperty.getBinary();
        CheckArg.isNotNull(binaryValue, "binary");
        Node sequencedNode = outputNode;
        if (outputNode.isNew()) {
            outputNode.setPrimaryType(METADATA_NODE);
        } else {
            sequencedNode = outputNode.addNode(METADATA_NODE, METADATA_NODE);
        }
        PDDocument document = PDDocument.load(binaryValue.getStream());
        PDDocumentInformation information = document.getDocumentInformation();

        sequencedNode.setProperty(PAGES, document.getNumberOfPages());
        sequencedNode.setProperty(JcrConstants.JCR_MIME_TYPE, MimeTypeConstants.PDF);

        setPropertyIfMetadataPresent(sequencedNode, AUTHOR, information.getAuthor());
        setPropertyIfMetadataPresent(sequencedNode, CREATION_DATE, information.getCreationDate());
        setPropertyIfMetadataPresent(sequencedNode, CREATOR, information.getCreator());
        setPropertyIfMetadataPresent(sequencedNode, KEYWORDS, information.getKeywords());
        setPropertyIfMetadataPresent(sequencedNode, MODIFICATION_DATE, information.getModificationDate());
        setPropertyIfMetadataPresent(sequencedNode, PRODUCER, information.getProducer());
        setPropertyIfMetadataPresent(sequencedNode, SUBJECT, information.getSubject());
        setPropertyIfMetadataPresent(sequencedNode, TITLE, information.getTitle());
        return true;
    }

    private void setPropertyIfMetadataPresent(Node node, String propertyName, Object value) throws RepositoryException {
        if (value != null) {
            if (value instanceof String && !StringUtil.isBlank((String) value)) {
                node.setProperty(propertyName, (String) value);
            }
            if (value instanceof Long) {
                node.setProperty(propertyName, (Long) value);
            }
            if (value instanceof Integer) {
                node.setProperty(propertyName, (Integer) value);
            }
            if (value instanceof Calendar) {
                node.setProperty(propertyName, (Calendar) value);
            }
        }
    }
}

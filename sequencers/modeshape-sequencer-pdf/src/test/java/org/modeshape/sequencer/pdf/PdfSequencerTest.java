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

import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.junit.Test;
import org.modeshape.jcr.api.JcrConstants;
import org.modeshape.jcr.sequencer.AbstractSequencerTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

/**
 * Unit test for {@link PdfMetadataSequencer}
 *
 */
public class PdfSequencerTest extends AbstractSequencerTest {

    @Test
    public void shouldSequencePdf() throws Exception {
        createNodeWithContentFromFile("sample.pdf", "sample.pdf");
        Node outputNode = getOutputNode(rootNode, "sample.pdf/" + METADATA_NODE);
        assertSequencedPdf(outputNode);
    }

    private void assertSequencedPdf( Node sequencedNode ) throws RepositoryException {
        assertEquals(METADATA_NODE, sequencedNode.getPrimaryNodeType().getName());
        assertEquals("application/pdf", sequencedNode.getProperty(JcrConstants.JCR_MIME_TYPE).getString());
        assertEquals("lilylee", sequencedNode.getProperty(AUTHOR).getString());
        assertEquals("PScript5.dll Version 5.2.2", sequencedNode.getProperty(CREATOR).getString());
        assertEquals("Acrobat Distiller 7.0.5 (Windows)", sequencedNode.getProperty(PRODUCER).getString());
        assertEquals("Microsoft Word - Document1", sequencedNode.getProperty(TITLE).getString());
        assertEquals(1L, sequencedNode.getProperty(PAGES).getLong());
        assertEquals(2006, sequencedNode.getProperty(MODIFICATION_DATE).getDate().get(Calendar.YEAR));
        assertEquals(9, sequencedNode.getProperty(CREATION_DATE).getDate().get(Calendar.MONTH));
        assertFalse(sequencedNode.hasProperty(KEYWORDS));
        assertFalse(sequencedNode.hasProperty(SUBJECT));
    }
}

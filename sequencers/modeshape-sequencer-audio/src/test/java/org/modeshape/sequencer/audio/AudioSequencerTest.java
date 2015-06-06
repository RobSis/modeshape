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
package org.modeshape.sequencer.audio;

import static org.junit.Assert.assertEquals;
import static org.modeshape.sequencer.audio.AudioMetadataLexicon.FORMAT_NAME;
import static org.modeshape.sequencer.audio.AudioMetadataLexicon.BITRATE;
import static org.modeshape.sequencer.audio.AudioMetadataLexicon.SAMPLE_RATE;
import static org.modeshape.sequencer.audio.AudioMetadataLexicon.CHANNELS;
import static org.modeshape.sequencer.audio.AudioMetadataLexicon.LENGTH;
import static org.modeshape.sequencer.audio.AudioMetadataLexicon.ALBUM;
import static org.modeshape.sequencer.audio.AudioMetadataLexicon.AUTHOR;
import static org.modeshape.sequencer.audio.AudioMetadataLexicon.COMMENT;
import static org.modeshape.sequencer.audio.AudioMetadataLexicon.METADATA_NODE;
import static org.modeshape.sequencer.audio.AudioMetadataLexicon.TAG_NODE;
import static org.modeshape.sequencer.audio.AudioMetadataLexicon.TITLE;
import static org.modeshape.sequencer.audio.AudioMetadataLexicon.YEAR;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.junit.Test;
import org.modeshape.jcr.api.JcrConstants;
import org.modeshape.jcr.sequencer.AbstractSequencerTest;

/**
 * Unit test for {@link AudioMetadataSequencer}
 *
 */
public class AudioSequencerTest extends AbstractSequencerTest {

    @Test
    public void shouldSequenceMp3() throws Exception {
        createNodeWithContentFromFile("sample.mp3", "sample1.mp3");

        Node sequencedNodeSameLocation = getOutputNode(rootNode, "sample.mp3/" + METADATA_NODE);
        assertSequencedMp3(sequencedNodeSameLocation);

        Node sequencedNodeDifferentLocation = getOutputNode(rootNode, "sequenced/audio/sample.mp3");
        assertSequencedMp3(sequencedNodeDifferentLocation);
    }

    private void assertSequencedMp3( Node sequencedNode ) throws RepositoryException {
        assertEquals(METADATA_NODE, sequencedNode.getPrimaryNodeType().getName());
        assertEquals("mp3", sequencedNode.getProperty(FORMAT_NAME).getString());
        assertEquals("audio/mpeg", sequencedNode.getProperty(JcrConstants.JCR_MIME_TYPE).getString());
        assertEquals("64", sequencedNode.getProperty(BITRATE).getString());
        assertEquals("44100", sequencedNode.getProperty(SAMPLE_RATE).getString());
        assertEquals("Joint Stereo", sequencedNode.getProperty(CHANNELS).getString());
        assertEquals("2", sequencedNode.getProperty(LENGTH).getString());

        Node tagNode = sequencedNode.getNode(TAG_NODE);
        assertEquals("Badwater Slim Performs Live", tagNode.getProperty(ALBUM).getString());
        assertEquals("Badwater Slim", tagNode.getProperty(AUTHOR).getString());
        assertEquals("This is a test audio file.", tagNode.getProperty(COMMENT).getString());
        assertEquals("Sample MP3", tagNode.getProperty(TITLE).getString());
        assertEquals("2008", tagNode.getProperty(YEAR).getString());
    }

    @Test
    public void shouldSequenceOggVorbis() throws Exception {
        createNodeWithContentFromFile("vorbis.ogg", "vorbis.ogg");

        Node sequencedNodeSameLocation = getOutputNode(rootNode, "vorbis.ogg/" + METADATA_NODE);
        assertSequencedOggVorbis(sequencedNodeSameLocation);

        Node sequencedNodeDifferentLocation = getOutputNode(rootNode, "sequenced/audio/vorbis.ogg");
        assertSequencedOggVorbis(sequencedNodeDifferentLocation);
    }

    private void assertSequencedOggVorbis( Node sequencedNode ) throws RepositoryException {
        assertEquals(METADATA_NODE, sequencedNode.getPrimaryNodeType().getName());
        assertEquals("ogg", sequencedNode.getProperty(FORMAT_NAME).getString());
        assertEquals("audio/x-vorbis+ogg", sequencedNode.getProperty(JcrConstants.JCR_MIME_TYPE).getString());
        assertEquals("112", sequencedNode.getProperty(BITRATE).getString());
        assertEquals("44100", sequencedNode.getProperty(SAMPLE_RATE).getString());
        assertEquals("2", sequencedNode.getProperty(CHANNELS).getString());
        assertEquals("2", sequencedNode.getProperty(LENGTH).getString());

        Node tagNode = sequencedNode.getNode(TAG_NODE);
        assertEquals("Badwater Slim Performs Live", tagNode.getProperty(ALBUM).getString());
        assertEquals("Badwater Slim", tagNode.getProperty(AUTHOR).getString());
        assertEquals("This is a test audio file.", tagNode.getProperty(COMMENT).getString());
        assertEquals("Sample OGG", tagNode.getProperty(TITLE).getString());
        assertEquals("2008", tagNode.getProperty(YEAR).getString());
    }

    @Test
    public void shouldSequenceFlac() throws Exception {
        createNodeWithContentFromFile("sample.flac", "sample.flac");

        Node sequencedNodeSameLocation = getOutputNode(rootNode, "sample.flac/" + METADATA_NODE);
        assertSequencedFlac(sequencedNodeSameLocation);

        Node sequencedNodeDifferentLocation = getOutputNode(rootNode, "sequenced/audio/sample.flac");
        assertSequencedFlac(sequencedNodeDifferentLocation);
    }

    private void assertSequencedFlac( Node sequencedNode ) throws RepositoryException {
        assertEquals(METADATA_NODE, sequencedNode.getPrimaryNodeType().getName());
        assertEquals("flac", sequencedNode.getProperty(FORMAT_NAME).getString());
        assertEquals("audio/flac", sequencedNode.getProperty(JcrConstants.JCR_MIME_TYPE).getString());
        assertEquals("429", sequencedNode.getProperty(BITRATE).getString());
        assertEquals("44100", sequencedNode.getProperty(SAMPLE_RATE).getString());
        assertEquals("2", sequencedNode.getProperty(CHANNELS).getString());
        assertEquals("2", sequencedNode.getProperty(LENGTH).getString());

        Node tagNode = sequencedNode.getNode(TAG_NODE);
        assertEquals("Badwater Slim Performs Live", tagNode.getProperty(ALBUM).getString());
        assertEquals("Badwater Slim", tagNode.getProperty(AUTHOR).getString());
        assertEquals("This is a test audio file.", tagNode.getProperty(COMMENT).getString());
        assertEquals("Sample FLAC", tagNode.getProperty(TITLE).getString());
        assertEquals("2008", tagNode.getProperty(YEAR).getString());
    }
}

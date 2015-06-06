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

import java.io.IOException;
import java.io.InputStream;

import javax.jcr.Binary;
import javax.jcr.ItemExistsException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.modeshape.common.util.CheckArg;
import org.modeshape.common.util.StringUtil;
import org.modeshape.jcr.api.JcrConstants;
import org.modeshape.jcr.api.nodetype.NodeTypeManager;
import org.modeshape.jcr.api.sequencer.Sequencer;

/**
 * A sequencer that processes the binary content of an audio file, extracts the metadata for the file, and then writes that
 * audio metadata to the repository.
 * <p>
 * This sequencer produces data that corresponds to the following structure:
 * <ul>
 * <li><strong>audio:metadata</strong> node of type <code>audio:metadata</code>
 * <ul>
 * <li><strong>jcr:mimeType</strong> - optional string property for the mime type of the file</li>
 * <li><strong>audio:bitrate</strong> option long property for specifying the bitrate</li>
 * <li><strong>audio:sampleRate</strong> optional int property for specifying the sample rate</li>
 * <li><strong>audio:channels</strong> optional string property for specifying the channel mode</li>
 * <li><strong>audio:length</strong> optional int property specifying estimation of length</li>
 * <li><strong>audio:tag</strong> - optional child node which contains additional id3 (or other) tag information (if available)</li>
 * <ul>
 * <li><strong>audio:title</strong> - optional string property for the name of the audio file or recording</li>
 * <li><strong>audio:author</strong> - optional string property for the author of the recording</li>
 * <li><strong>audio:album</strong> - optional string property for the name of the album</li>
 * <li><strong>audio:year</strong> - optional string property for the year the recording as created</li>
 * <li><strong>audio:comment</strong> - optional string property specifying a comment</li>
 * </ul>
 * </ul>
 * </li>
 * </ul>
 * </p>
 */
public class AudioMetadataSequencer extends Sequencer {

    @Override
    public void initialize( NamespaceRegistry registry,
                            NodeTypeManager nodeTypeManager ) throws RepositoryException, IOException {
        super.registerNodeTypes("audio.cnd", nodeTypeManager, true);
        registerDefaultMimeTypes(AudioMetadata.MIME_TYPE_STRINGS);
    }

    @Override
    public boolean execute( Property inputProperty,
                            Node outputNode,
                            Context context ) throws Exception {
        Binary binaryValue = inputProperty.getBinary();
        CheckArg.isNotNull(binaryValue, "binary");
        AudioMetadata metadata = null;
        try (InputStream stream = binaryValue.getStream()) {
            metadata = new AudioMetadata();
            metadata.setInput(stream);
            metadata.check();
        }
        Node sequencedNode = outputNode;
        if (outputNode.isNew()) {
            outputNode.setPrimaryType(AudioMetadataLexicon.METADATA_NODE);
        } else {
            sequencedNode = outputNode.addNode(AudioMetadataLexicon.METADATA_NODE, AudioMetadataLexicon.METADATA_NODE);
        }

        sequencedNode.setProperty(AudioMetadataLexicon.FORMAT_NAME, metadata.getFormatName());
        setPropertyIfMetadataPresent(sequencedNode, JcrConstants.JCR_MIME_TYPE, metadata.getMimeType());
        setPropertyIfMetadataPresent(sequencedNode, AudioMetadataLexicon.BITRATE, metadata.getBitrate());
        setPropertyIfMetadataPresent(sequencedNode, AudioMetadataLexicon.SAMPLE_RATE, metadata.getSampleRate());
        setPropertyIfMetadataPresent(sequencedNode, AudioMetadataLexicon.CHANNELS, metadata.getChannels());
        setPropertyIfMetadataPresent(sequencedNode, AudioMetadataLexicon.LENGTH, metadata.getLength());

        addTagNode(sequencedNode, metadata);
        return true;
    }

    private void addTagNode( Node sequencedNode,
                             AudioMetadata metadata ) throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, RepositoryException {
        Node tagNode = sequencedNode.addNode(AudioMetadataLexicon.TAG_NODE, AudioMetadataLexicon.TAG_NODE);

        setPropertyIfMetadataPresent(tagNode, AudioMetadataLexicon.TITLE, metadata.getTitle());
        setPropertyIfMetadataPresent(tagNode, AudioMetadataLexicon.AUTHOR, metadata.getAuthor());
        setPropertyIfMetadataPresent(tagNode, AudioMetadataLexicon.ALBUM, metadata.getAlbum());
        setPropertyIfMetadataPresent(tagNode, AudioMetadataLexicon.YEAR, metadata.getYear());
        setPropertyIfMetadataPresent(tagNode, AudioMetadataLexicon.COMMENT, metadata.getComment());
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
        }
    }

}
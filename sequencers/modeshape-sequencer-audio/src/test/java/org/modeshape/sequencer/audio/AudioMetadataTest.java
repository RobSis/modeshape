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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;

/**
 * Unit test for {@link AudioMetadata}
 */
public class AudioMetadataTest {

    private InputStream getTestAudio( String resourcePath ) {
        return this.getClass().getClassLoader().getResourceAsStream(resourcePath);
    }

    @Test
    public void shouldBeAbleToLoadMp3() throws Exception {
        AudioMetadata metadata = new AudioMetadata();

        metadata.setInput(getTestAudio("sample1.mp3"));
        assertTrue(metadata.check());

        assertThat(metadata.getFormatName(), is("mp3"));
        assertThat(metadata.getMimeType(), is("audio/mpeg"));
        assertThat(metadata.getBitrate(), is(64L));
        assertThat(metadata.getSampleRate(), is(44100));
        assertThat(metadata.getChannels(), is("Joint Stereo"));
        assertThat(metadata.getLength(), is(2));
        assertThat(metadata.getAlbum(), is("Badwater Slim Performs Live"));
        assertThat(metadata.getAuthor(), is("Badwater Slim"));
        assertThat(metadata.getComment(), is("This is a test audio file."));
        assertThat(metadata.getTitle(), is("Sample MP3"));
        assertThat(metadata.getYear(), is("2008"));
    }

    @Test
    public void shouldBeAbleToLoadOggVorbis() throws Exception {
        AudioMetadata metadata = new AudioMetadata();

        metadata.setInput(getTestAudio("vorbis.ogg"));
        assertTrue(metadata.check());

        assertThat(metadata.getFormatName(), is("ogg"));
        assertThat(metadata.getMimeType(), is("audio/x-vorbis+ogg"));
        assertThat(metadata.getBitrate(), is(112L));
        assertThat(metadata.getSampleRate(), is(44100));
        assertThat(metadata.getChannels(), is("2"));
        assertThat(metadata.getLength(), is(2));
        assertThat(metadata.getAlbum(), is("Badwater Slim Performs Live"));
        assertThat(metadata.getAuthor(), is("Badwater Slim"));
        assertThat(metadata.getComment(), is("This is a test audio file."));
        assertThat(metadata.getTitle(), is("Sample OGG"));
        assertThat(metadata.getYear(), is("2008"));
    }

    @Test
    public void shouldBeAbleToLoadFlac() throws Exception {
        AudioMetadata metadata = new AudioMetadata();

        metadata.setInput(getTestAudio("sample.flac"));
        assertTrue(metadata.check());

        assertThat(metadata.getFormatName(), is("flac"));
        assertThat(metadata.getMimeType(), is("audio/flac"));
        assertThat(metadata.getBitrate(), is(429L));
        assertThat(metadata.getSampleRate(), is(44100));
        assertThat(metadata.getChannels(), is("2"));
        assertThat(metadata.getLength(), is(2));
        assertThat(metadata.getAlbum(), is("Badwater Slim Performs Live"));
        assertThat(metadata.getAuthor(), is("Badwater Slim"));
        assertThat(metadata.getComment(), is("This is a test audio file."));
        assertThat(metadata.getTitle(), is("Sample FLAC"));
        assertThat(metadata.getYear(), is("2008"));
    }
}

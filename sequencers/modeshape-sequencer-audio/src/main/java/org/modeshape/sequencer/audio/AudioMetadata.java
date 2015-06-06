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

import static org.jaudiotagger.tag.FieldKey.ALBUM;
import static org.jaudiotagger.tag.FieldKey.ARTIST;
import static org.jaudiotagger.tag.FieldKey.COMMENT;
import static org.jaudiotagger.tag.FieldKey.TITLE;
import static org.jaudiotagger.tag.FieldKey.YEAR;

import java.io.DataInput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.flac.FlacFileReader;
import org.jaudiotagger.audio.mp3.MP3FileReader;
import org.jaudiotagger.audio.ogg.OggFileReader;
import org.jaudiotagger.tag.Tag;

/**
 * Utility for extracting metadata from audio files.
 */
public class AudioMetadata {

    /**
     * Return value of {@link #getFormat()} for Mpeg streams. AudioMetadata can extract metadata and audio information from
     * Mpegs. It is determined whether the stream is progressive (see {@link #isProgressive()}).
     */
    public static final int FORMAT_MPEG = 0;

    /**
     * Return value of {@link #getFormat()} for Vorbis streams. AudioMetadata can extract metadata and audio information from
     * Vorbis stream and Ogg container. It is determined whether the stream is interlaced (see {@link #isProgressive()}).
     */
    public static final int FORMAT_VORBIS = 1;

    /**
     * Return value of {@link #getFormat()} for FLAC streams. AudioMetadata can extract metadata and audio information from
     * FLAC stream and Ogg container. It is determined whether the stream is interlaced (see {@link #isProgressive()}).
     */
    public static final int FORMAT_FLAC = 2;

    /**
     * Return value of {@link #getFormat()} for WMA streams. AudioMetadata can extract metadata and audio information from
     * WMA stream and ASF container. It is determined whether the stream is interlaced (see {@link #isProgressive()}).
     */
    public static final int FORMAT_ASF = 3;

    public static final int FORMAT_MP4 = 4;

    public static final int FORMAT_REAL = 5;

    public static final int FORMAT_WAV = 6;


    /**
     * The names of the MIME types for all supported file formats.
     */
    static final String[] MIME_TYPE_STRINGS = {"audio/mpeg",
                                               "audio/vorbis", "audio/x-vorbis", "audio/ogg",
                                               "audio/flac", "audio/x-flac",
                                               "audio/x-ms-wma", "audio/x-ms-asf"};

    /**
     * The extensions of all supported file formats. The FORMAT_xyz int constants can be used as index values for
     * this array.
     */
    static final String[] FORMAT_NAMES = {"mp3","ogg", "flac", "wma"};

    private int format;
    private InputStream in;
    private DataInput din;
    private AudioFile audioFile;

    private Long bitrate;
    private Integer sampleRate;
    private Integer length;
    private String channels;

    private String title;
    private String author;
    private String album;
    private String year;
    private String comment;
    private String mimeType;

    public AudioMetadata() {
        AudioFileIO.logger.getParent().setLevel(Level.OFF);
    }

    public boolean check() {
        format = -1;
        try {
            // create a temporary copy from input
            File fileCopy = File.createTempFile("modeshape-sequencer-audio", ".tmp");
            FileOutputStream fileOutputStream = new FileOutputStream(fileCopy);
            byte[] b = new byte[1024];
            while (read(b) != -1) {
                fileOutputStream.write(b);
            }
            fileOutputStream.close();
            // FIXME: use tika instead?
            mimeType = Files.probeContentType(fileCopy.toPath());

            if (mimeType.startsWith("audio/mpeg")) {
                format = FORMAT_MPEG;
                audioFile = new MP3FileReader().read(fileCopy);
            } else if (mimeType.startsWith("audio/vorbis") || mimeType.startsWith("audio/x-vorbis")) {
                format = FORMAT_VORBIS;
                audioFile = new OggFileReader().read(fileCopy);
            } else if (mimeType.startsWith("audio/flac") || mimeType.startsWith("audio/x-flac")) {
                format = FORMAT_FLAC;
                audioFile = new FlacFileReader().read(fileCopy);
            }
            return checkSupportedAudio();
        } catch (Exception e) {
            //log
        }
        return false;
    }

    /**
     * Parse tags common for all audio files.
     */
    private boolean checkSupportedAudio() {
            try {
                AudioHeader header = audioFile.getAudioHeader();
                bitrate = header.getBitRateAsNumber();
                sampleRate = header.getSampleRateAsNumber();
                channels = header.getChannels();
                length = header.getTrackLength();

                Tag tag = audioFile.getTag();
                author = tag.getFirst(ARTIST);
                album = tag.getFirst(ALBUM);
                title = tag.getFirst(TITLE);
                comment = tag.getFirst(COMMENT);
                year = tag.getFirst(YEAR);

                return true;
            } catch (Exception e) {
                // log
            }
        return false;
    }

    /**
     * If {@link #check()} was successful, returns the audio format as one of the FORMAT_xyz constants from this class. Use
     * {@link #getFormatName()} to get a textual description of the file format.
     *
     * @return file format as a FORMAT_xyz constant
     */
    public int getFormat() {
        return format;
    }

    /**
     * If {@link #check()} was successful, returns the audio format's name. Use {@link #getFormat()} to get a unique number.
     * 
     * @return file format name
     */
    public String getFormatName() {
        if (format >= 0 && format < FORMAT_NAMES.length) {
            return FORMAT_NAMES[format];
        }
        return "?";
    }

    /**
     * If {@link #check()} was successful, returns a String with the MIME type of the format.
     * 
     * @return MIME type, e.g. <code>audio/mpeg</code>
     */
    public String getMimeType() {
        return mimeType;
    }

    public Long getBitrate() {
        return bitrate;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public String getChannels() {
        return channels;
    }

    public Integer getLength() {
        return length;
    }
//
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getAlbum() {
        return album;
    }

    public String getYear() {
        return year;
    }

    public String getComment() {
        return comment;
    }

    private int read( byte[] a ) throws IOException {
        if (in != null) {
            return in.read(a);
        }
        din.readFully(a);
        return a.length;
    }

    /**
     * Set the input stream to the argument stream (or file). Note that {@link java.io.RandomAccessFile} implements
     * {@link java.io.DataInput}.
     *
     * @param dataInput the input stream to read from
     */
    public void setInput( DataInput dataInput ) {
        din = dataInput;
        in = null;
    }

    /**
     * Set the input stream to the argument stream (or file).
     *
     * @param inputStream the input stream to read from
     */
    public void setInput( InputStream inputStream ) {
        in = inputStream;
        din = null;
    }
}

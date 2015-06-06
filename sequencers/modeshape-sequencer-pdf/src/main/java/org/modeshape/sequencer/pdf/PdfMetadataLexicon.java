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

import static org.modeshape.sequencer.pdf.PdfMetadataLexicon.Namespace.PREFIX;

import org.modeshape.common.annotation.Immutable;


/**
 * A lexicon of names used within PDF sequencer.
 */
@Immutable
public class PdfMetadataLexicon {

    public static class Namespace {
        public static final String URI = "http://www.modeshape.org/pdf/1.0";
        public static final String PREFIX = "pdf";
    }

    public static final String METADATA_NODE = PREFIX + ":metadata";

}

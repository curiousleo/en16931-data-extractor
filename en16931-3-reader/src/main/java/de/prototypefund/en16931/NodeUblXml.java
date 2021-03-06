
/*
 * Copyright 2019 Svante Schubert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.prototypefund.en16931;

import static de.prototypefund.en16931.NodeSemantic.LEADING_TRAILING_WHITESPACES;
import de.prototypefund.en16931.type.Type;
import de.prototypefund.en16931.type.UblDataType;

/**
 * Represents an UBL XML syntax node using special UBL data types.
 */
public class NodeUblXml extends NodeXml {

    public NodeUblXml(String path, NodeSemantic semanticParent) {
        super(path, semanticParent);
    }

    public Type getType() {
        return mType;
    }

    public void setType(String type) {
        mType = UblDataType.getByValue(type.replaceAll(LEADING_TRAILING_WHITESPACES, ""), this.getSemanticNode().getId());
    }
}

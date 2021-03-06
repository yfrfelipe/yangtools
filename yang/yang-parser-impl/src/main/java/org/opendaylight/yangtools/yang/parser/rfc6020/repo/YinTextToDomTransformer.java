/*
 * Copyright (c) 2016 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.parser.rfc6020.repo;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.Futures;
import java.io.IOException;
import javax.xml.parsers.SAXParser;
import javax.xml.transform.dom.DOMSource;
import org.opendaylight.yangtools.util.xml.UntrustedXML;
import org.opendaylight.yangtools.yang.model.repo.api.SchemaRepository;
import org.opendaylight.yangtools.yang.model.repo.api.YinDomSchemaSource;
import org.opendaylight.yangtools.yang.model.repo.api.YinTextSchemaSource;
import org.opendaylight.yangtools.yang.model.repo.spi.SchemaSourceRegistry;
import org.opendaylight.yangtools.yang.model.repo.util.SchemaSourceTransformer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A {@link SchemaSourceTransformer} which handles translation of models from
 * {@link YinTextSchemaSource} representation into {@link YinDomSchemaSource}.
 *
 * @author Robert Varga
 */
@Beta
public final class YinTextToDomTransformer extends SchemaSourceTransformer<YinTextSchemaSource, YinDomSchemaSource> {
    private YinTextToDomTransformer(final SchemaRepository provider, final SchemaSourceRegistry consumer) {
        super(provider, YinTextSchemaSource.class, consumer, YinDomSchemaSource.class,
            input -> Futures.immediateCheckedFuture(transformSource(input)));
    }

    public static YinTextToDomTransformer create(final SchemaRepository provider, final SchemaSourceRegistry consumer) {
        return new YinTextToDomTransformer(provider, consumer);
    }

    public static YinDomSchemaSource transformSource(final YinTextSchemaSource source) throws SAXException,
            IOException {
        final Document doc = UntrustedXML.newDocumentBuilder().newDocument();
        final SAXParser parser = UntrustedXML.newSAXParser();
        final DefaultHandler handler = new StatementSourceReferenceHandler(doc, null);
        parser.parse(source.openStream(), handler);
        return YinDomSchemaSource.create(source.getIdentifier(), new DOMSource(doc));
    }
}

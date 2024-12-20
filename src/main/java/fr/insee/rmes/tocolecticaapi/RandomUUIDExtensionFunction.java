package fr.insee.rmes.tocolecticaapi;

import net.sf.saxon.s9api.*;

import java.util.UUID;


public class RandomUUIDExtensionFunction implements ExtensionFunction{

    @Override
    public QName getName() {
        return new QName("http://some.namespace.com", "randomUUID");
    }

    @Override
    public SequenceType getResultType() {
        return SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE_OR_MORE);
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[] {};
    }

    @Override
    public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
        UUID result = UUID.randomUUID();
        return new XdmAtomicValue(result.toString());
    }

}

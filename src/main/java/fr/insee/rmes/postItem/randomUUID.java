package fr.insee.rmes.postItem;

import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.ItemType;
import net.sf.saxon.s9api.OccurrenceIndicator;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.SequenceType;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmValue;

import java.util.UUID;

//import location.to.test.java.file.Test;
public class randomUUID implements ExtensionFunction{

    @Override
    public QName getName() {
        return new QName("http://some.namespace.com", "randomUUID");
    }

    @Override
    public SequenceType getResultType() {
        return SequenceType.makeSequenceType(ItemType.STRING, OccurrenceIndicator.ONE_OR_MORE);
    }

    @Override
    public net.sf.saxon.s9api.SequenceType[] getArgumentTypes() {
        return new SequenceType[] {};
    }

    @Override
    public XdmValue call(XdmValue[] arguments) throws SaxonApiException {
        UUID result = UUID.randomUUID();
        return new XdmAtomicValue(result.toString());
    }

}

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
                xmlns:r="ddi:reusable:3_3"
                xmlns:ddi="ddi:instance:3_3"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <xsl:output method="text"/>

    <xsl:template match="/">
        <xsl:text>Transformation result: </xsl:text>
        <xsl:value-of select="(//r:Agency)[1]"/>
    </xsl:template>
</xsl:stylesheet>
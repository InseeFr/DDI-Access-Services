<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:msxsl="urn:schemas-microsoft-com:xslt" exclude-result-prefixes="msxsl"
                xmlns:l="ddi:logicalproduct:3_3"
                xmlns:r="ddi:reusable:3_3"
                xmlns:ext="java:java.lang.System">

    <xsl:output method="text" indent="yes" />

    <!-- Define keys for faster lookups -->
    <xsl:key name="varByURN" match="l:Variable" use="r:URN" />
    <xsl:key name="catByURN" match="l:Category" use="r:URN" />
    <xsl:key name="codeListByURN" match="l:CodeList" use="r:URN" />

    <xsl:template match="/">
        <xsl:text>[&#10;</xsl:text>

        <xsl:for-each select="//l:DataRelationship">
            <xsl:variable name="dataRelation_URN" select="r:URN" />
            <xsl:variable name="dataRelation_name" select="l:LogicalRecord/l:LogicalRecordName/r:String" />
            <xsl:variable name="dataRelation_libelle_fr" select="r:Label/r:Content" />

            <xsl:if test="position() > 1">
                <xsl:text>,&#10;</xsl:text>
            </xsl:if>

            <xsl:text>  {&#10;    "id": "</xsl:text>
            <xsl:value-of select="$dataRelation_URN" />
            <xsl:text>",&#10;    "nom": "</xsl:text>
            <xsl:value-of select="normalize-space($dataRelation_name)" />
            <xsl:text>",&#10;    "label": [&#10;      { "contenu": "</xsl:text>
            <xsl:value-of select="normalize-space($dataRelation_libelle_fr)" />
            <xsl:text>", "langue": "fr" },&#10;      { "contenu": "", "langue": "en" }&#10;    ],&#10;    "variables": [&#10;</xsl:text>

            <xsl:for-each select="l:LogicalRecord/l:VariablesInRecord/l:Variable">
                <xsl:variable name="Variable_URN" select="r:URN" />
                <xsl:variable name="Variable_name" select="l:VariableName/r:String" />
                <xsl:variable name="Variable_label" select="r:Label/r:Content" />

                <xsl:if test="position() > 1">
                    <xsl:text>,&#10;</xsl:text>
                </xsl:if>
                <xsl:text>      { "id": "</xsl:text>
                <xsl:value-of select="$Variable_URN" />
                <xsl:text>",&#10;        "nom": "</xsl:text>
                <xsl:value-of select="normalize-space($Variable_name)" />
                <xsl:text>",&#10;        "label": [&#10;          { "contenu": "</xsl:text>
                <xsl:value-of select="normalize-space($Variable_label)" />
                <xsl:text>", "langue": "fr" },&#10;          { "contenu": "", "langue": "en" }&#10;        ],&#10;        "ordre": "</xsl:text>
                <xsl:number />
                <xsl:text>",&#10;        "representation": </xsl:text>

                <xsl:choose>
                    <xsl:when test="l:RepresentedVariable/r:NumericRepresentation">
                        <xsl:text>"numerique"</xsl:text>
                    </xsl:when>
                    <xsl:when test="l:RepresentedVariable/r:TextRepresentation">
                        <xsl:text>"texte"</xsl:text>
                    </xsl:when>
                    <xsl:when test="l:RepresentedVariable/r:DateTimeRepresentation">
                        <xsl:text>"date"</xsl:text>
                    </xsl:when>
                    <xsl:when test="l:RepresentedVariable/r:CodeRepresentation">
                        <xsl:text>"codes"</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>"null"</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>

                <xsl:text>&#10;      }</xsl:text>
            </xsl:for-each>
            <xsl:text>&#10;    ]&#10;  }</xsl:text>
        </xsl:for-each>
        <xsl:text>&#10;]</xsl:text>
    </xsl:template>
</xsl:stylesheet>

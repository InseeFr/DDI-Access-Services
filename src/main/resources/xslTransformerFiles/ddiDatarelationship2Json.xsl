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
            <xsl:variable name="dataRelation_name" select="l:DataRelationshipName/r:String" />
            <xsl:variable name="dataRelation_libelle_fr" select="r:Label/r:Content" />
            <xsl:variable name="dataRelation_Version" select="r:Version" />
            <xsl:variable name="dataRelation_Versiondate" select="@versionDate" />


            <xsl:if test="position() > 1">
                <xsl:text>,&#10;</xsl:text>
            </xsl:if>

            <xsl:text>  {&#10;    "id": "</xsl:text>
            <xsl:value-of select="$dataRelation_URN" />
            <xsl:text>",&#10;    "version": "</xsl:text>
            <xsl:value-of select="$dataRelation_Version" />
            <xsl:text>",&#10;    "versionDate": "</xsl:text>
            <xsl:value-of select="$dataRelation_Versiondate" />
            <xsl:text>",&#10;    "nom": "</xsl:text>
            <xsl:value-of select="$dataRelation_name" />
            <xsl:text>",&#10;    "label": [&#10;      { "contenu": "</xsl:text>
            <xsl:value-of select="normalize-space($dataRelation_libelle_fr)" />
            <xsl:text>", "langue": "fr" },&#10;      { "contenu": "", "langue": "en" }&#10;    ],&#10;    "variables": [&#10;</xsl:text>

            <xsl:for-each select="l:LogicalRecord/l:VariablesInRecord/l:VariableUsedReference">
                <xsl:variable name="VariableUsed_Agency" select="r:Agency" />
                <xsl:variable name="VariableUsed_ID" select="r:ID" />
                <xsl:variable name="VariableUsed_Version" select="r:Version" />



                <!--                isGeographic="true"-->
                <xsl:variable name="VariableUsed_URN" select="concat('urn:ddi:', $VariableUsed_Agency, ':', $VariableUsed_ID, ':', $VariableUsed_Version)" />

                <xsl:variable name="var" select="key('varByURN', $VariableUsed_URN)" />

                <xsl:if test="position() > 1">
                    <xsl:text>,&#10;</xsl:text>
                </xsl:if>
                <xsl:text>      { "id": "</xsl:text>
                <xsl:value-of select="$var/r:URN" />
                <xsl:text>",&#10;        "nom": "</xsl:text>
                <xsl:value-of select="normalize-space($var/l:VariableName/r:String)" />
                <xsl:text>",&#10;        "label": [&#10;          { "contenu": "</xsl:text>
                <xsl:value-of select="normalize-space($var/r:Label/r:Content)" />
                <xsl:text>", "langue": "fr" },&#10;          { "contenu": "", "langue": "en" }&#10;        ],&#10;        "ordre": "</xsl:text>
                <xsl:number />
                <xsl:text>",&#10;        "role": "</xsl:text>
                <xsl:choose>
                    <xsl:when test="not(normalize-space($var/l:VariableRepresentation/l:VariableRole))">
                        <!-- Si la valeur est vide, appliquer une valeur par défaut -->
                        <xsl:value-of select="'Mesure'" />
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- Sinon, afficher la valeur existante -->
                        <xsl:value-of select="normalize-space($var/l:VariableRepresentation/l:VariableRole)" />
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:text>",&#10;        "variablegeographique": "</xsl:text>
                <xsl:value-of select="normalize-space($var/@isGeographic)" />
                <xsl:text>",&#10;        "representation": {</xsl:text>
                <xsl:text>"type":</xsl:text>
                <xsl:choose>
                    <xsl:when test="$var/l:VariableRepresentation/r:CodeRepresentation">
                        <xsl:text>"codes"</xsl:text>
                    </xsl:when>
                    <xsl:when test="$var/l:VariableRepresentation/r:NumericRepresentation">
                        <xsl:variable name="NumericTypeCode" select="$var/l:VariableRepresentation/r:NumericRepresentation/r:NumericTypeCode" />
                        <xsl:choose>
                            <xsl:when test="not(normalize-space($NumericTypeCode))">
                                <!-- Si la valeur est vide, appliquer une valeur par défaut -->
                                <xsl:text>"numerique"</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <!-- Sinon, afficher la valeur existante -->
                                <xsl:text>"</xsl:text><xsl:value-of select="$NumericTypeCode" /><xsl:text>"</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>

                    </xsl:when>
                    <xsl:when test="$var/l:VariableRepresentation/r:TextRepresentation">
                        <xsl:text>"texte"</xsl:text>
                    </xsl:when>
                    <xsl:when test="$var/l:VariableRepresentation/r:DateTimeRepresentation">
                        <xsl:text>"date"</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>"null"</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:text>}</xsl:text>
                <!-- Process Code Representation -->
                <xsl:if test="$var/l:VariableRepresentation/r:CodeRepresentation">
                    <xsl:text>,&#10;        "controles": { "codes": [&#10;</xsl:text>
                    <xsl:variable name="CodeListURN" select="concat('urn:ddi:', $var/l:VariableRepresentation/r:CodeRepresentation/r:CodeListReference/r:Agency, ':', $var/l:VariableRepresentation/r:CodeRepresentation/r:CodeListReference/r:ID, ':', $var/l:VariableRepresentation/r:CodeRepresentation/r:CodeListReference/r:Version)" />
                    <xsl:for-each select="key('codeListByURN', $CodeListURN)/l:Code">
                        <xsl:if test="position() > 1">
                            <xsl:text>,&#10;</xsl:text>
                        </xsl:if>
                        <xsl:text>          { "code": "</xsl:text>
                        <xsl:value-of select="r:Value" />
                        <xsl:text>", "label": [ { "langue": "fr", "contenu": "</xsl:text>
                        <xsl:variable name="CategoryURN" select="concat('urn:ddi:', r:CategoryReference/r:Agency, ':', r:CategoryReference/r:ID, ':', r:CategoryReference/r:Version)" />
                        <xsl:value-of select="normalize-space(key('catByURN', $CategoryURN)/r:Label/r:Content)" />
                        <xsl:text>" }, { "langue": "en", "contenu": "" } ] }</xsl:text>
                    </xsl:for-each>
                    <xsl:text>&#10;        ] }</xsl:text>
                </xsl:if>

                <xsl:text>&#10;      }</xsl:text>
            </xsl:for-each>
            <xsl:text>&#10;    ]&#10;  }</xsl:text>
        </xsl:for-each>
        <xsl:text>&#10;]</xsl:text>


    </xsl:template>
</xsl:stylesheet>
